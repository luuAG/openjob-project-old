package com.openjob.admin.skill;

import com.openjob.common.model.Skill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
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
        return skillRepo.save(skill);
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

    public Page<Skill> getNotVerified(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return skillRepo.findUnverifiedSkill(pageable);
    }

    public void deleteByName(String name) {
        skillRepo.deleteByName(name);
    }
}
