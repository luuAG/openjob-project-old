package com.openjob.admin.skill;

import com.openjob.common.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {

    @Query("update Skill s set s.isVerified=true where s.id = ?1")
    @Modifying
    void verifySkill(Integer skillId);

    Optional<Skill> findByName(String name);

    @Query("select s from Skill s where s.specialization.id = ?1")
    Collection<Skill> getBySpecialization(Integer speId);
}
