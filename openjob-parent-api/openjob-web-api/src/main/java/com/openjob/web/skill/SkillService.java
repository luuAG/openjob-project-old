package com.openjob.web.skill;

import com.openjob.common.model.Skill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SkillService {
    @Autowired
    private SkillRepository skillRepo;


    public Collection<Skill> getBySpecialization(Integer speId) {
        return skillRepo.findBySpecialization(speId);
    }

    public boolean checkExist(Integer id) {
        return skillRepo.existsById(id);
    }
}
