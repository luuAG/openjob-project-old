package com.openjob.web.statistics;

import com.openjob.common.enums.CvStatus;
import com.openjob.common.model.JobCvTracking;
import com.openjob.web.dto.CvStatisticModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


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
        List<CvStatisticDTO> toReturn = new ArrayList<>();
        Map<String, CvStatisticDTO> map = new HashMap<>();
        Pageable pageable = PageRequest.of(0, 10);
        List<CvStatisticModel> cvStatisticModelList;
        if (startDate != null && endDate != null){
            cvStatisticModelList = jobCvTrackingRepo.findCvStatisticWithDateRange(companyId, startDate, endDate, pageable);
        } else {
            cvStatisticModelList = jobCvTrackingRepo.findCvStatistic(companyId, pageable);
        }
        cvStatisticModelList.forEach(record -> {
            if (!map.containsKey(record.getJobTitle()))
                map.put(record.getJobTitle(), new CvStatisticDTO(
                        record.getJobTitle(),
                        record.getJobCreatedAt(),
                        1L,
                        record.getCvStatus().name().equals(CvStatus.ACCEPTED.name()) ? 1L : 0L,
                        record.getCvStatus().name().equals(CvStatus.REJECTED.name()) ? 1L : 0L
                ));
            else {
                CvStatisticDTO tempDTO = map.get(record.getJobTitle());
                tempDTO.setAppliedCv(tempDTO.getAppliedCv() + 1);
                tempDTO.setAcceptedCv(record.getCvStatus().name().equals(CvStatus.ACCEPTED.name()) ? tempDTO.getAcceptedCv() + 1: tempDTO.getAcceptedCv());
                tempDTO.setRejectedCv(record.getCvStatus().name().equals(CvStatus.REJECTED.name()) ? tempDTO.getRejectedCv() + 1: tempDTO.getRejectedCv());
            }

        });
        map.forEach((key, value) -> {
            toReturn.add(value);
        });
        return toReturn;
    }
}
