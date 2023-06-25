package com.openjob.admin.business;


import com.openjob.admin.company.CompanyRepository;
import com.openjob.admin.company.CompanyService;
import com.openjob.common.model.OpenjobBusiness;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@EnableAsync
public class OpenjobBusinessService {
    private final OpenjobBusinessRepository repository;
    private final CompanyRepository companyRepository;

    public OpenjobBusiness get(){
        return repository.findAll().get(0);
    }

    public OpenjobBusiness update(OpenjobBusiness openjobBusiness){
        OpenjobBusiness data = get();

        data.setMaxTimeForFreeJobInDays(openjobBusiness.getMaxTimeForFreeJobInDays());

        data.setBaseJobPricePerDay(openjobBusiness.getBaseJobPricePerDay());
        data.setBaseCvViewPrice(openjobBusiness.getBaseCvViewPrice());

        data.setFreeCvView(openjobBusiness.getFreeCvView());
        data.setFreeJob(openjobBusiness.getFreeJob());

        data.setFresherWeight(openjobBusiness.getFresherWeight());
        data.setInternWeight(openjobBusiness.getInternWeight());
        data.setJuniorWeight(openjobBusiness.getJuniorWeight());
        data.setMiddleWeight(openjobBusiness.getMiddleWeight());
        data.setSeniorWeight(openjobBusiness.getSeniorWeight());
        data.setHighPositionWeight(openjobBusiness.getHighPositionWeight());

        data.setPremiumFreeViewCv(openjobBusiness.getPremiumFreeViewCv());
        data.setPremiumFreeJob(openjobBusiness.getPremiumFreeJob());
        data.setPremiumPrice(openjobBusiness.getPremiumPrice());

        return repository.save(data);
    }

    @Scheduled(cron = "0 0 0 1 * *")
    @Async
    public void resetFreeServiceForAllCompanies(){
        OpenjobBusiness openjobBusiness = get();
        companyRepository.resetFreeServiceForAll(openjobBusiness.getFreeJob(), openjobBusiness.getFreeCvView());
        companyRepository.resetPremiumServiceForAll(openjobBusiness.getPremiumFreeJob(), openjobBusiness.getPremiumFreeViewCv());
    }
}

