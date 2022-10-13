package com.openjob.admin.skill;

import com.openjob.common.model.Skill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SkillService {
    @Autowired
    private SkillRepository skillRepo;


    public void verifySkill (Integer skillId){
        skillRepo.verifySkill(skillId);
    }

    public Collection<Skill> getAll(){
        return skillRepo.findAll();
    }

    public Skill save(Skill specialization){
        return skillRepo.save(specialization);
    }

    public void delete(Integer id) {
        skillRepo.delete(skillRepo.getById(id));
    }

    public boolean checkExistByName(String name) {
        return skillRepo.findByName(name).isPresent();
    }

    public Collection<Skill> getBySpecialization(Integer speId) {
        return skillRepo.getBySpecialization(speId);
    }
}
