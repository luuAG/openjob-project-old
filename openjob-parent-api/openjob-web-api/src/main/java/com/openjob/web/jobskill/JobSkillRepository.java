package com.openjob.web.jobskill;

import com.openjob.common.model.JobSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobSkillRepository extends JpaRepository<JobSkill, Integer> {

    @Query("select js from JobSkill js where js.job.id=?1 and js.skill.id=?2")
    Optional<JobSkill> findByJobAndSkill(String jobId, Integer skillId);

}
