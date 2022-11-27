package com.openjob.web.jobcv;

import com.openjob.common.enums.CvStatus;
import com.openjob.common.model.CV;
import com.openjob.common.model.Job;
import com.openjob.common.model.JobCV;
import com.openjob.web.cv.CvRepository;
import com.openjob.web.exception.ResourceNotFoundException;
import com.openjob.web.job.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class JobCvService {
    private final JobCvRepository jobCvRepo;
    private final CvRepository cvRepo;
    private final JobRepository jobRepo;

    public void saveNewApplication(String cvId, String jobId) {
        Optional<Job> job = jobRepo.findById(jobId);
        Optional<CV> cv = cvRepo.findById(cvId);
        if (job.isPresent() && cv.isPresent()){
            JobCV jobCV = new JobCV();
            jobCV.setJob(job.get());
            jobCV.setCv(cv.get());
            jobCV.setApplyDate(new Date());
            jobCV.setIsApplied(true);
            jobCV.setStatus(CvStatus.NEW);
            jobCvRepo.save(jobCV);
        } else
            throw new IllegalArgumentException("CV or Job not found!");
    }

    public void deleteApplication(String cvId, String jobId) {
        jobCvRepo.deleteByCvIdAndJobId(cvId, jobId);
    }


    public void acceptCV(String jobId, String cvId) {
        Optional<Job> job = jobRepo.findById(jobId);
        Optional<CV> cv = cvRepo.findById(cvId);
        if (job.isPresent() && cv.isPresent()){
            Optional<JobCV>  existingJobCv = jobCvRepo.findByJobIdAndCvId(jobId, cvId);
            if (existingJobCv.isPresent()){
                existingJobCv.get().setStatus(CvStatus.ACCEPTED);
                jobCvRepo.save(existingJobCv.get());
            } else
                throw new ResourceNotFoundException("JobCV", "jobId, cvId", jobId + ", " + cvId);

        }

    }

    public void rejectCV(String jobId, String cvId) {
        Optional<Job> job = jobRepo.findById(jobId);
        Optional<CV> cv = cvRepo.findById(cvId);
        if (job.isPresent() && cv.isPresent()){
            Optional<JobCV>  existingJobCv = jobCvRepo.findByJobIdAndCvId(jobId, cvId);
            if (existingJobCv.isPresent()){
                existingJobCv.get().setStatus(CvStatus.REJECTED);
                jobCvRepo.save(existingJobCv.get());
            } else
                throw new ResourceNotFoundException("JobCV", "jobId, cvId", jobId + ", " + cvId);

        }
    }

    public CvStatus getStatus(String jobId, String cvId){
        Optional<JobCV> jobCV = jobCvRepo.findByJobIdAndCvId(jobId, cvId);
        return jobCV.orElseThrow().getStatus();
    }

    public Optional<JobCV> getByJobIdAndCvId(String jobId, String cvId) {
        return jobCvRepo.findByJobIdAndCvId(jobId, cvId);
    }

    public JobCV save(JobCV existingJobCv) {
        return jobCvRepo.save(existingJobCv);
    }
}
