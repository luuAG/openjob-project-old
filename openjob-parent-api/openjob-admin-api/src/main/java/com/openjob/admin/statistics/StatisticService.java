package com.openjob.admin.statistics;

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
    public void trackJob(CompanyStatistic companyStatistic){
        companyStatisticRepo.save(companyStatistic);
    }

    @Async
    public void trackCvApply(JobCvTracking jobCvTracking){
        jobCvTrackingRepo.save(jobCvTracking);
    }
}
