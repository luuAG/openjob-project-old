package com.openjob.admin.job;

import com.openjob.common.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {

    @Query(value = """
            select j
            from Job j
            left join j.jobSkills se where se.skill.isVerified=false
            """)
    Page<Job> findAllwithSkillnotVerified(Pageable pageable);
}
