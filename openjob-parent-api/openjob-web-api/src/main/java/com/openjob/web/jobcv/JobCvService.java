package com.openjob.web.jobcv;

import com.openjob.common.enums.CvStatus;
import com.openjob.common.enums.MailCase;
import com.openjob.common.model.*;
import com.openjob.web.cv.CvRepository;
import com.openjob.web.exception.ResourceNotFoundException;
import com.openjob.web.job.JobRepository;
import com.openjob.web.setting.SettingService;
import com.openjob.web.statistics.StatisticService;
import com.openjob.web.util.CustomJavaMailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class JobCvService {
    private final JobCvRepository jobCvRepo;
    private final CvRepository cvRepo;
    private final JobRepository jobRepo;
    private final SettingService settingService;
    private final CustomJavaMailSender mailSender;
    private final StatisticService statisticService;

    public void saveNewApplication(String cvId, String jobId) {
        Job job;
        Optional<JobCV> jobCV = jobCvRepo.findByJobIdAndCvId(jobId, cvId);
        if (jobCV.isPresent()) {
            jobCV.get().setIsApplied(true);
            jobCV.get().setStatus(CvStatus.NEW);
            jobCV.get().setApplyDate(new Date());
            jobCvRepo.save(jobCV.get());
            job = jobCV.get().getJob();
        } else {
            job = jobRepo.findById(jobId).orElseThrow();
            Optional<CV> cv = cvRepo.findById(cvId);
            if (cv.isPresent()){
                JobCV newJobCv = new JobCV();
                newJobCv.setJob(job);
                newJobCv.setCv(cv.get());
                newJobCv.setIsApplied(true);
                newJobCv.setStatus(CvStatus.NEW);
                newJobCv.setApplyDate(new Date());
                jobCvRepo.save(newJobCv);
            } else
                throw new IllegalArgumentException("CV or Job not found!");
        }
        // mail to company
        MailSetting mailSetting = new MailSetting(
                job.getCompany().getEmail(),
                "Đã có ứng viên cho tin tuyển dụng",
                settingService.getByMailCase(MailCase.MAIL_JOB_HAS_APPLICATION).getValue(),
                null,
                job.getCompany(),
                job,
                null);
        mailSender.sendMail(mailSetting); // async

        // tracking for statistics
        JobCvTracking jobCvTracking = new JobCvTracking();
        jobCvTracking.setJobId(jobId);
        jobCvTracking.setApplyDate(new Date());
        jobCvTracking.setCvId(cvId);
        jobCvTracking.setCvStatus(CvStatus.NEW);
        statisticService.trackCvApply(jobCvTracking);


    }

    public void deleteApplication(String cvId, String jobId) {
        Optional<JobCV> jobCV = jobCvRepo.findByJobIdAndCvId(jobId, cvId);
        if (jobCV.isPresent()) {
            if (Objects.nonNull(jobCV.get().getIsMatched())  && jobCV.get().getIsMatched()){
                jobCV.get().setIsApplied(false);
                jobCvRepo.save(jobCV.get());
            } else {
                jobCvRepo.deleteByCvIdAndJobId(cvId, jobId);
            }
        }
    }


    public void acceptCV(String jobId, String cvId) {
        Optional<Job> job = jobRepo.findById(jobId);
        Optional<CV> cv = cvRepo.findById(cvId);
        if (job.isPresent() && cv.isPresent()){
            Optional<JobCV>  existingJobCv = jobCvRepo.findByJobIdAndCvId(jobId, cvId);
            if (existingJobCv.isPresent()){
                existingJobCv.get().setStatus(CvStatus.ACCEPTED);
                jobCvRepo.save(existingJobCv.get());

                // tracking for statistics
                statisticService.updateCvStatus(jobId, cvId, CvStatus.ACCEPTED);
            } else
                throw new ResourceNotFoundException("JobCV", "jobId, cvId", jobId + ", " + cvId);

        } else
            throw new EntityNotFoundException("Job/CV not found!");

    }

    public void rejectCV(String jobId, String cvId) {
        Optional<Job> job = jobRepo.findById(jobId);
        Optional<CV> cv = cvRepo.findById(cvId);
        if (job.isPresent() && cv.isPresent()){
            Optional<JobCV>  existingJobCv = jobCvRepo.findByJobIdAndCvId(jobId, cvId);
            if (existingJobCv.isPresent()){
                existingJobCv.get().setStatus(CvStatus.REJECTED);
                jobCvRepo.save(existingJobCv.get());

                // tracking for statistics
                statisticService.updateCvStatus(jobId, cvId, CvStatus.ACCEPTED);
            } else
                throw new ResourceNotFoundException("JobCV", "jobId, cvId", jobId + ", " + cvId);

        }
    }

    public Optional<JobCV> getByJobIdAndCvId(String jobId, String cvId) {
        return jobCvRepo.findByJobIdAndCvId(jobId, cvId);
    }

    public JobCV save(JobCV existingJobCv) {
        return jobCvRepo.save(existingJobCv);
    }

    public Boolean checkUserAppliedJob(String userId, String jobId) {
        Optional<JobCV> jobCV = jobCvRepo.findByUserIdAndJobId(userId, jobId);
        if (jobCV.isPresent())
            return jobCV.get().getIsApplied();
        return false;
    }

    public void deleteByJobId(String jobId) {
        jobCvRepo.deleteByJobId(jobId);
    }

    public Page<JobCV> searchJobCv(Specification<JobCV> jobCvSpec, Pageable pageable) {
        return jobCvRepo.findAll(jobCvSpec, pageable);
    }

    public List<User> getUserAppliedJob(String jobId) {
        return jobCvRepo.findUserAppliedJob(jobId);
    }

    public List<JobCV> getByJobId(String jobId) {
        return jobCvRepo.findByJobId(jobId);
    }
}
