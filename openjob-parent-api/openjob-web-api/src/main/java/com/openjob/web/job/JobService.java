package com.openjob.web.job;

import com.openjob.common.model.*;
import com.openjob.web.cv.CvRepository;
import com.openjob.web.jobcvmatching.JobCVMatchingRepository;
import com.openjob.web.skill.SkillRepository;
import com.openjob.web.util.JobCVUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepo;
    private final SkillRepository skillRepo;
    private final JobCVMatchingRepository jobCVMatchingRepo;
    private final CvRepository cvRepo;



    public Optional<Job> getById(String id) {
        return jobRepo.findById(id);
    }

    public Job saveNewJob(Job job) throws SQLException {
        List<JobSkill> realListJobSkill = new ArrayList<>();

        // detect new skill
        for (int i = 0; i < job.getJobSkills().size(); i++) {
            JobSkill JSfromRequest =  job.getJobSkills().get(i);

            if (Objects.nonNull(JSfromRequest.getSkill().getId())){
                realListJobSkill.add(JSfromRequest);
            } else {
                Skill skillFromRequest = JSfromRequest.getSkill();
                skillFromRequest.setIsVerified(false);
                skillFromRequest.setSpecialization(job.getSpecialization());

                Skill savedSkill = skillRepo.save(skillFromRequest);
                JSfromRequest.setSkill(savedSkill);
                realListJobSkill.add(JSfromRequest);
            }
        }

        job.setJobSkills(realListJobSkill);

        job.setCreatedAt(new Date());
        return jobRepo.save(job);
    }

    public Page<Job> searchByKeywordAndLocationAndCompany(Integer size, Integer page, String keyword, String location, String companyId) {
        Page<Job> pageJob;
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(location) || location.isBlank()){
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
        if (Objects.nonNull(companyId) && !companyId.isBlank()){
            List<Job> jobs = new ArrayList<>(pageJob.getContent());
            for (int i=0; i< jobs.size(); i++)
                if ( ! jobs.get(i).getCompany().getId().equals(companyId))
                    jobs.remove(jobs.get(i));

            return new PageImpl<>(jobs);
        }
        return pageJob;
    }

    public void deleteById(String jobId){
        jobRepo.deleteById(jobId);
    }

    @Async
    public void findCVmatchJob(Job savedJob) {
        List<CV> listCV = cvRepo.findBySpecialization(savedJob.getSpecialization().getId());

        for (CV cv : listCV) {
            int matchingPoint = JobCVUtils.checkCVmatchJob(savedJob, cv);
            if (matchingPoint > 0){
                JobCvMatching jobCvMatching = new JobCvMatching();
                jobCvMatching.setJob(savedJob);
                jobCvMatching.setCv(cv);
                jobCvMatching.setPoint(matchingPoint);
                jobCVMatchingRepo.save(jobCvMatching);
            }
        }

    }
}
