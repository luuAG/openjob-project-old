package com.openjob.admin.job;

import com.openjob.common.model.JobCV;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class JobCvService {
    private final JobCvRepository jobCvRepo;

    public Optional<JobCV> getByJobIdAndCvId(String jobId, String cvId) {
        return jobCvRepo.findByJobIdAndCvId(jobId, cvId);
    }

    public void save(JobCV jobCV) {
        jobCvRepo.save(jobCV);
    }
}
