package com.openjob.web.util;

import com.openjob.common.model.Company;
import com.openjob.common.model.OpenjobBusiness;
import com.openjob.web.business.OpenjobBusinessService;
import com.openjob.web.company.CompanyService;
import com.openjob.web.dto.JobRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class PriceCalculationUtils {
    private final OpenjobBusinessService openjobBusinessService;
    private final CompanyService companyService;

    public double calculateJobPrice(String companyId, JobRequestDTO job){
        OpenjobBusiness businessParameters = openjobBusinessService.get();

        Date date1 = new Date();
        Date date2 = job.getExpiredAt();
        long difference = Math.abs(date2.getTime() - date1.getTime());
        long deviationInDays = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS) - 1;

        Company company = companyService.getById(companyId);

        // if company still has free job post
        if (company.getAmountOfFreeJobs() > 0 && deviationInDays <= businessParameters.getMaxTimeForFreeJobInDays()){
            return 0;
        }
        // calculate as openjob business
        double finalPrice;


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


        finalPrice = businessParameters.getBaseJobPricePerDay()
                * weight
                * deviationInDays;

        return finalPrice;
    }
}
