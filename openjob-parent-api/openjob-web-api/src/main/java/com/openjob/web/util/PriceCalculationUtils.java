package com.openjob.web.util;

import com.openjob.common.enums.JobLevel;
import com.openjob.common.model.Company;
import com.openjob.common.model.Job;
import com.openjob.common.model.OpenjobBusiness;
import com.openjob.web.business.OpenjobBusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class PriceCalculationUtils {
    private final OpenjobBusinessService openjobBusinessService;

    public double calculateJobPrice(Company company, Job job){
        // if company still has free job post
        if (company.getAmountOfFreeJobs() > 0){
            return 0;
        }
        // calculate as openjob business
        double finalPrice;
        OpenjobBusiness businessParameters = openjobBusinessService.get();

        double weight;
        String jobLevel = job.getJobLevel().name();
        switch (jobLevel){
            case "INTERNSHIP" -> weight = businessParameters.getInternWeight();
            case "FRESHER" -> weight = businessParameters.getFresherWeight();
            case "JUNIOR" -> weight = businessParameters.getJuniorWeight();
            case "MIDDLE" -> weight = businessParameters.getMiddleWeight();
            case "SENIOR" -> weight = businessParameters.getSeniorWeight();
            default -> weight = businessParameters.getHighPositionWeight();
        }

        Date date1 = job.getCreatedAt();
        Date date2 = job.getExpiredAt();

        long difference = Math.abs(date2.getTime() - date1.getTime());
        long deviationInDays = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
        finalPrice = businessParameters.getBaseJobPricePerDay()
                * weight
                * deviationInDays;

        return finalPrice;
    }
}
