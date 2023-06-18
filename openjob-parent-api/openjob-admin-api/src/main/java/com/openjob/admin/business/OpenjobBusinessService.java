package com.openjob.admin.business;


import com.openjob.common.model.OpenjobBusiness;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OpenjobBusinessService {
    private final OpenjobBusinessRepository repository;

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
}

