package com.openjob.web.skillexperience;

import com.openjob.common.enums.ExperienceValue;
import com.openjob.common.model.SkillExperience;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillExperienceRepository extends JpaRepository<SkillExperience, Integer> {

    @Query("select se from SkillExperience se where se.skill.name=?1 and se.experience.value=?2")
    Page<SkillExperience> findBySkillAndExperience(String skill, ExperienceValue experience, Pageable pageable);
}
