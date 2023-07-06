package com.openjob.web.statistics;

import com.openjob.common.enums.CvStatus;
import com.openjob.common.model.JobCvTracking;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@EnableAsync
@Service
@Transactional
@RequiredArgsConstructor
public class StatisticService {
    private final JobCvTrackingRepository jobCvTrackingRepo;

    @Async
    public void trackCvApply(JobCvTracking jobCvTracking){
        jobCvTrackingRepo.save(jobCvTracking);
    }

    @Async
    public void updateCvStatus(String jobId, String cvId, CvStatus cvStatus){
        jobCvTrackingRepo.updateCvStatus(jobId, cvId, cvStatus);
    }


    public List<CvStatisticDTO> getCvStatistic(String companyId, Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(0, 10);
        if (startDate != null && endDate != null){
            return jobCvTrackingRepo.findCvStatisticWithDateRange(companyId, startDate, endDate, pageable);
        } else {
            return jobCvTrackingRepo.findCvStatistic(companyId, pageable);
        }
    }
}
