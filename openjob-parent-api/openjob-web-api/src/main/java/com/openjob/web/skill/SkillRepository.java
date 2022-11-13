package com.openjob.web.skill;

import com.openjob.common.enums.ExperienceValue;
import com.openjob.common.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {
    Optional<Skill> findByNameAndExperience(String name, ExperienceValue experience);

    @Query("select s from Skill s where s.specialization.id=?1 group by s.name")
    Collection<Skill> findBySpecialization(Integer speId);

    @Query("select case when count(s)>0 then true else false end from Skill s where s.name=?1")
    boolean existsByName(String name);
}
