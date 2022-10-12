package com.openjob.admin.skill;

import com.openjob.common.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {

    @Query("update Skill s set s.isVerified=true where s.id = ?1")
    @Modifying
    void verifySkill(Integer skillId);
}
