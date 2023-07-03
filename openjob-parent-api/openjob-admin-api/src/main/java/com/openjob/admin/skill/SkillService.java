package com.openjob.admin.skill;

import com.openjob.common.model.Skill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SkillService {
    @Autowired
    private SkillRepository skillRepo;

    public Collection<Skill> getAll(){
        return skillRepo.findAll();
    }

    public Skill save(Skill skill){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        skill.setUpdatedAt(new Date());
        skill.setUpdatedBy(username);
        if (skill.getId() == null) {
            skill.setCreatedAt(new Date());
            skill.setCreatedBy(username);
        }
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

    public Page<Skill> getNotVerified(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return skillRepo.findUnverifiedSkill(pageable);
    }

    public void deleteByName(String name) {
        skillRepo.deleteByName(name);
    }

    public void verifyManySkills(List<Integer> skillIds) {
        skillRepo.verifyManySkills(skillIds);
    }

    public Page<Skill> search(Specification<Skill> skillSpec, Pageable pageable) {
        return skillRepo.findAll(skillSpec, pageable);
    }

    public Skill getById(Integer id) {
        return skillRepo.findById(id).orElseThrow();
    }
}
