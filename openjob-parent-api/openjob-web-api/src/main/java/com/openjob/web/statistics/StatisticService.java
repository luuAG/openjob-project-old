package com.openjob.web.statistics;

import com.openjob.common.enums.CvStatus;
import com.openjob.common.model.CompanyStatistic;
import com.openjob.common.model.JobCvTracking;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@EnableAsync
@Service
@Transactional
@RequiredArgsConstructor
public class StatisticService {
    private final CompanyStatisticRepository companyStatisticRepo;
    private final JobCvTrackingRepository jobCvTrackingRepo;

    @Async
    public void trackCvApply(JobCvTracking jobCvTracking){
        jobCvTrackingRepo.save(jobCvTracking);
    }

    @Async
    public void updateCvStatus(String jobId, String cvId, CvStatus cvStatus){
        jobCvTrackingRepo.updateCvStatus(jobId, cvId, cvStatus);
    }
}
