package com.openjob.admin.statistics;

import com.openjob.admin.trackinginvoice.InvoiceRepository;
import com.openjob.admin.webuser.WebUserRepository;
import com.openjob.common.enums.ServiceType;
import com.openjob.common.model.CompanyStatistic;
import com.openjob.common.model.Invoice;
import com.openjob.common.model.User;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
    private final CompanyStatisticRepository companyStatisticRepo;
    private final WebUserRepository userRepo;
    private final InvoiceRepository invoiceRepo;

    @Async
    public void trackJob(CompanyStatistic companyStatistic){
        companyStatisticRepo.save(companyStatistic);
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
            jobStatisticDTO.getAmountOfJobs().add(count);
        }

        return jobStatisticDTO;
    }

    public List<CvCompanyDTO> getCvStatistic(String companyId) {
        if (StringUtils.isEmpty(companyId)){
            Pageable pageable = PageRequest.of(0, 10);
            return companyStatisticRepo.findTop10MostCvAppliedCompanies(pageable);
        }
        return companyStatisticRepo.findCvCompanyStatisticByCompanyId(companyId);
    }

    public UserStatisticDTO getUserStatistic() {
        UserStatisticDTO userStatisticDTO = new UserStatisticDTO();
        userStatisticDTO.setAmountOfUsers(new ArrayList<>(12));

        List<User> users = userRepo.findAllUser();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i<12; i++){
            final int tempMonth = i;
            int count = (int) users.stream()
                    .filter(user -> {
                        calendar.setTime(user.getCreatedAt() == null? new Date() : user.getCreatedAt());
                        return calendar.get(Calendar.MONTH) == tempMonth;
                    })
                    .count();
            userStatisticDTO.getAmountOfUsers().add(count);
        }
        return userStatisticDTO;
    }

    public Map<String, Double> getIncomeStatistic() {
        Map<String, Double> toReturn = new HashMap<>();
        toReturn.put(ServiceType.JOB_POST.name(), 0.0);
        toReturn.put(ServiceType.VIEW_CV.name(), 0.0);
        toReturn.put(ServiceType.UPGRADE_MEMBERSHIP.name(), 0.0);
        List<Invoice> invoices = invoiceRepo.findAll();
        invoices.forEach(invoice -> {
            switch (invoice.getServiceType()){
                case VIEW_CV -> toReturn.put(
                        ServiceType.VIEW_CV.name(),
                        toReturn.get(ServiceType.VIEW_CV.name()) + invoice.getAmount()
                );
                case JOB_POST, UPDATE_JOB -> toReturn.put(
                        ServiceType.JOB_POST.name(),
                        toReturn.get(ServiceType.JOB_POST.name()) + invoice.getAmount()
                );
                case UPGRADE_MEMBERSHIP -> toReturn.put(
                        ServiceType.UPGRADE_MEMBERSHIP.name(),
                        toReturn.get(ServiceType.UPGRADE_MEMBERSHIP.name()) + invoice.getAmount()
                );
                case REFUND -> toReturn.put(
                        ServiceType.JOB_POST.name(),
                        toReturn.get(ServiceType.JOB_POST.name()) - invoice.getAmount()
                );
            }
        });
        return toReturn;
    }
}
