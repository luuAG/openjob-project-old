package com.openjob.web.statistics;

import com.openjob.common.enums.CvStatus;
import com.openjob.common.model.CompanyStatistic;
import com.openjob.common.model.JobCvTracking;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    public JobStatisticDTO getJobStatistic() {
        JobStatisticDTO jobStatisticDTO = new JobStatisticDTO();
        jobStatisticDTO.setAmountOfJobs(new ArrayList<>(12));
        List<CompanyStatistic> companyStatisticsList = companyStatisticRepo.findAll();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i<12; i++){
            final int tempMonth = i;
            int count = (int) companyStatisticsList.stream()
                    .filter(item -> {
                        calendar.setTime(item.getJobCreatedAt());
                        return calendar.get(Calendar.MONTH) == tempMonth;
                    })
                    .count();

        }

        return null;
    }
}
