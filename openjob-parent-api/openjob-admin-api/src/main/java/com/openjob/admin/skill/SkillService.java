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

    public Skill save(Skill skill){
        Skill existingSkill = skillRepo.getById(skill.getId());
        existingSkill.setName(skill.getName());
        return skillRepo.save(existingSkill);
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

    public boolean checkExist(Integer id) {
        return skillRepo.existsById(id);
    }
}
