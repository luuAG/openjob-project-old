package com.openjob.web.skill;

import com.openjob.common.enums.ExperienceValue;
import com.openjob.common.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {
    Optional<Skill> findByNameAndExperience(String name, ExperienceValue experience);
}
