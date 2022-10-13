package com.openjob.admin.specialization;

import com.openjob.common.model.Specialization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SpecializationService {
    @Autowired
    private SpecializationRepository specializationRepo;

    public Collection<Specialization> getAll(){
        return specializationRepo.findAll();
    }

    public Collection<Specialization> getByMajor(Integer majorId){
        return specializationRepo.findByMajor(majorId);
    }

    public Specialization save(Specialization specialization){
        return specializationRepo.save(specialization);
    }

    public boolean checkExist(Integer id) {
        return specializationRepo.existsById(id);
    }

    public void delete(Integer id) {
        specializationRepo.delete(specializationRepo.getById(id));
    }

    public boolean checkExistByName(String name) {
        return specializationRepo.findByName(name).isPresent();
    }
}
