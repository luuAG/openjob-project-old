package com.openjob.web.job;

import com.openjob.common.enums.JobStatus;
import com.openjob.common.enums.Role;
import com.openjob.common.model.*;
import com.openjob.web.business.OpenjobBusinessService;
import com.openjob.web.company.CompanyService;
import com.openjob.web.dto.JobRequestDTO;
import com.openjob.web.dto.JobResponseDTO;
import com.openjob.web.dto.JobSkillDTO;
import com.openjob.web.jobcv.JobCvService;
import com.openjob.web.jobskill.JobSkillRepository;
import com.openjob.web.skill.SkillRepository;
import com.openjob.web.specialization.SpecializationService;
import com.openjob.web.user.UserService;
import com.openjob.web.util.AuthenticationUtils;
import com.openjob.web.util.NullAwareBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    private final CompanyService companyService;
    private final SpecializationService speService;
    private final JobSkillRepository jobSkillRepo;
    private final AuthenticationUtils authenticationUtils;
    private final OpenjobBusinessService openjobBusinessService;




    public Optional<Job> getById(String id) {
        return jobRepo.findById(id);
    }

    public Job saveUpdate(JobRequestDTO jobDTO, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException, IOException {
        // validation
        Company company = companyService.getById(jobDTO.getCompanyId());
        Optional<Specialization> specialization = speService.getById(jobDTO.getSpecializationId());
        if (Objects.isNull(company) || specialization.isEmpty())
            throw new IllegalArgumentException("Company/Specialization not found!");

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
                skillFromRequest.setIsVerified(false);
                skillFromRequest.setCreatedAt(new Date());
                skillFromRequest.setCreatedBy(authenticationUtils.getLoggedInUser(request).getFirstName());
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

        companyService.updateAccountBalance(company.getId(), - jobDTO.getJobPrice());
        return jobRepo.save(savedJob);
    }

    public void deleteById(String jobId) {
        jobRepo.deleteById(jobId);
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

    @Scheduled(cron = "0 0 0 * * *")
    @Async
    @PostConstruct
    public void updateStatusExpiredJob() {
        List<Job> expiredJob = jobRepo.findUnhiddenExpiredJob().stream().peek(job -> job.setJobStatus(JobStatus.HIDDEN)).collect(Collectors.toList());
        jobRepo.saveAll(expiredJob);
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
