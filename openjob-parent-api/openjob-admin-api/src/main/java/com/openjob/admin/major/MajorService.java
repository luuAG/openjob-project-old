package com.openjob.admin.major;

import com.openjob.common.model.Major;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class MajorService {
    @Autowired
    private MajorRepository majorRepo;

    public Collection<Major> getAll(){
        return majorRepo.findAll();
    }

    public Major save(Major major){
        return majorRepo.save(major);
    }

    public boolean checkExist(Integer id) {
        return majorRepo.existsById(id);
    }

    public void delete(Integer id) {
        majorRepo.delete(majorRepo.getById(id));
    }

    public boolean checkExistByName(String name) {
        return majorRepo.findByName(name).isPresent();
    }

    public Major getById(Integer majorId) {
        return majorRepo.getById(majorId);
    }
}
