package com.openjob.web.job;

import com.openjob.common.enums.CvStatus;
import com.openjob.common.enums.JobStatus;
import com.openjob.common.enums.Role;
import com.openjob.common.model.*;
import com.openjob.web.company.CompanyService;
import com.openjob.web.cv.CvRepository;
import com.openjob.web.dto.JobRequestDTO;
import com.openjob.web.dto.JobResponseDTO;
import com.openjob.web.dto.JobSkillDTO;
import com.openjob.web.jobcv.JobCvRepository;
import com.openjob.web.jobcv.JobCvService;
import com.openjob.web.jobskill.JobSkillRepository;
import com.openjob.web.major.MajorService;
import com.openjob.web.skill.SkillRepository;
import com.openjob.web.specialization.SpecializationService;
import com.openjob.web.user.UserService;
import com.openjob.web.util.JobCVUtils;
import com.openjob.web.util.NullAwareBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@EnableAsync
@Service
@Transactional
@RequiredArgsConstructor
public class JobService {
    private final UserService userService;
    private final JobRepository jobRepo;
    private final SkillRepository skillRepo;
    private final JobCvService jobCvService;
    private final JobCvRepository jobCvRepo;
    private final CvRepository cvRepo;
    private final CompanyService companyService;
    private final SpecializationService speService;
    private final JobSkillRepository jobSkillRepo;
    private final MajorService majorService;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");


    public Optional<Job> getById(String id) {
        return jobRepo.findById(id);
    }

    public Job saveUpdate(JobRequestDTO jobDTO) throws InvocationTargetException, IllegalAccessException {
        NullAwareBeanUtils beanCopier = NullAwareBeanUtils.getInstance();
        Job job;
        if (jobDTO.getId() == null) {
            job = new Job();
            job.setCreatedAt(new Date());
        }
        else {
            job = getById(jobDTO.getId()).orElseThrow();
            job.setUpdatedAt(new Date());
        }
        beanCopier.copyProperties(job, jobDTO);


        Company company = companyService.getById(jobDTO.getCompanyId());
        Optional<Specialization> specialization = speService.getById(jobDTO.getSpecializationId());
        if (Objects.isNull(company) || specialization.isEmpty())
            throw new IllegalArgumentException("Company/Specialization not found!");

        Major major = specialization.get().getMajor();
        job.setMajor(major);
        job.setCompany(company);
        job.setSpecialization(specialization.get());
        job.setJobStatus(JobStatus.NEW);

        job.getJobSkills().clear();
        Job savedJob = jobRepo.save(job);
        jobSkillRepo.deleteByJobId(savedJob.getId());

        // detect new skill
        for (int i = 0; i < jobDTO.getListJobSkillDTO().size(); i++) {
            JobSkillDTO JSfromRequest = jobDTO.getListJobSkillDTO().get(i);
            JobSkill jobSkill = new JobSkill();

            Skill skillFromRequest = JSfromRequest.getSkill();
            Optional<Skill> skillInDB = skillRepo.findByName(skillFromRequest.getName());
            // if new skill -> save new skill and set to jobSkill
            if (skillInDB.isEmpty()){
                skillFromRequest.setSpecialization(savedJob.getSpecialization());
//                skillFromRequest.setIsVerified(skillRepo.existsByName(skillFromRequest.getName()));
                skillFromRequest.setIsVerified(false);
                Skill savedSkill = skillRepo.save(skillFromRequest);
                jobSkill.setSkill(savedSkill);
            } else { // if existing skill -> set to jobSkill
                jobSkill.setSkill(skillInDB.get());
            }
            jobSkill.setRequired(JSfromRequest.getIsRequired());
            jobSkill.setJob(savedJob);
            jobSkill.setWeight(JSfromRequest.getWeight());
            jobSkill.setYoe(JSfromRequest.getYoe());
            savedJob.getJobSkills().add(jobSkillRepo.save(jobSkill));
        }

        return jobRepo.save(savedJob);
    }

    public Page<Job> searchByKeywordAndLocationAndCompany(Integer size, Integer page, String keyword, String location, String companyId) {
        Page<Job> pageJob;
        Pageable pageable = PageRequest.of(page, size, sort);
        if (Objects.isNull(location) || location.isBlank()) {
            if (Objects.isNull(keyword) || keyword.isBlank())
                pageJob = jobRepo.findAll(pageable);
            else
                pageJob = jobRepo.findByKeyword(keyword, pageable);
        } else {
            if (Objects.isNull(keyword) || keyword.isBlank())
                pageJob = jobRepo.findByLocation(location, pageable);
            else
                pageJob = jobRepo.findByKeywordAndLocation(keyword, location, pageable);
        }
        if (Objects.nonNull(companyId) && !companyId.isBlank()) {
            List<Job> jobs = new ArrayList<>(pageJob.getContent());
            for (int i = 0; i < jobs.size(); i++)
                if (!jobs.get(i).getCompany().getId().equals(companyId))
                    jobs.remove(jobs.get(i));

            return new PageImpl<>(jobs);
        }
        return pageJob;
    }

    public void deleteById(String jobId) {
        jobRepo.deleteById(jobId);
    }

    @Async
    public void findCVmatchJob(Job savedJob) {
        List<CV> listCV = cvRepo.findBySpecialization(savedJob.getSpecialization().getId());
        List<JobSkill> jobSkills = savedJob.getJobSkills();
        Set<Skill> mustHaveSkills = jobSkills.stream()
                .filter(JobSkill::isRequired)
                .map(JobSkill::getSkill)
                .collect(Collectors.toSet());
        // filter list CV by job requirement: must-have & yoe
        listCV = listCV.stream()
                .filter(cv -> { // must-have
                    Set<Skill> tempMustHaveSkills = new HashSet<>(mustHaveSkills);
                    List<Skill> cvSkill = cv.getSkills().stream().map(CvSkill::getSkill).collect(Collectors.toList());
                    cvSkill.forEach(tempMustHaveSkills::remove);
                    return tempMustHaveSkills.isEmpty();
                })
                .filter(cv -> { // yoe
                    Skill mutualSkill;
                    for (CvSkill cvSkill : cv.getSkills()){
                        for (JobSkill jobSkill : jobSkills){
                            if (Objects.equals(cvSkill.getSkill().getId(), jobSkill.getSkill().getId())){
                                mutualSkill = cvSkill.getSkill();
                                if (mustHaveSkills.contains(mutualSkill) && cvSkill.getYoe() < jobSkill.getYoe())
                                    return false;
                            }
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        for (CV cv : listCV) {
            double matchingPoint = JobCVUtils.scoreCv(savedJob, cv);
            if (matchingPoint > 0) {
                Optional<JobCV> existingJobCv =  jobCvService.getByJobIdAndCvId(savedJob.getId(), cv.getId());
                if (existingJobCv.isPresent()){
                    existingJobCv.get().setIsMatching(true);
                    existingJobCv.get().setPoint(matchingPoint);
                    jobCvService.save(existingJobCv.get());
                }
                else {
                    JobCV newJobCv = new JobCV();
                    newJobCv.setJob(savedJob);
                    newJobCv.setStatus(CvStatus.NEW);
                    newJobCv.setIsMatching(true);
                    newJobCv.setPoint(matchingPoint);
                    newJobCv.setCv(cv);
                    newJobCv.setApplyDate(null);
                    newJobCv.setIsApplied(false);
                    jobCvService.save(newJobCv);
                }
            }
        }

    }

    public Page<Job> getByCompanyId(Integer page, Integer size, String cId) {
        Pageable pageable = PageRequest.of(page, size, sort);
        return jobRepo.findByCompanyId(cId, pageable);
    }

    public Page<JobCV> getJobAppliedByUser(Integer page, Integer size, String userId) {
        Pageable pageable = PageRequest.of(page, size);
        return jobCvRepo.findJobAppliedByUserId(userId, pageable);
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Async
    public void delete7daysExpiredJob() {
        Date today = new Date();
        List<Job> expiredJob = getExpiredJob().stream()
                .filter(job -> TimeUnit.DAYS.convert(
                        today.getTime() - job.getExpiredAt().getTime(), TimeUnit.MILLISECONDS) >= 7)
                .collect(Collectors.toList());
        // delete from db
        expiredJob.forEach(job -> {
            jobCvService.deleteByJobId(job.getId());
            jobRepo.deleteById(job.getId());
        });
    }

    public List<Job> getExpiredJob() {
        return jobRepo.findExpiredJob();
    }

    public void setExpiredDate(String jobId, Date expiredDate) {
        Optional<Job> job = jobRepo.findById(jobId);
        if (job.isPresent()){
            job.get().setExpiredAt(expiredDate);
            jobRepo.save(job.get());
        } else
            throw new IllegalArgumentException("Job not found with id: " + jobId);
    }

    public List<Job> getRelevantJobs(Job job) {
        Specialization specialization = job.getSpecialization();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        return jobRepo.findBySpecialization(specialization.getId(), pageable);
    }


    public JobResponseDTO mapJobToJobResponseDTO(Job job, User loggedInUser){
        JobResponseDTO toReturn = new JobResponseDTO();
        NullAwareBeanUtils copier = NullAwareBeanUtils.getInstance();
        try {
            copier.copyProperties(toReturn, job);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        if (Objects.nonNull(loggedInUser) &&  !loggedInUser.getRole().equals(Role.HR))
            toReturn.setIsApplied(jobCvService.checkUserAppliedJob(loggedInUser.getId(),job.getId()));
        return  toReturn;
    }



    public Page<Job> search(Specification<Job> jobSpec, Pageable pageable) {
        return jobRepo.findAll(jobSpec, pageable);
    }

    public Page<Job> getSuggestionJobs(Pageable pageable, User loggedInUser) {
        if (loggedInUser == null) {
            loggedInUser = userService.getByEmail("duongvannam2001@gmail.com");
        }
        Set<Integer> skillIds = loggedInUser.getCv().getSkills().stream()
                .map(cvSkill -> cvSkill.getSkill().getId())
                .collect(Collectors.toSet());

        return jobRepo.findBySkillIds(skillIds, pageable);
    }
}
