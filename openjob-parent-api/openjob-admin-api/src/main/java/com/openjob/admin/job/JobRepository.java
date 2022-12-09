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

    @Query("select j from Job j where j.company.name like %?1% or j.title like %?1%")
    Page<Job> findAllWithKeyword(String keyword, Pageable pageable);

    @Query("select j from Job j where j.company.id=?1")
    Page<Job> findAllByCompanyId(String companyId, Pageable pageable);

    @Query("select j from Job j where j.company.id=?1 and (j.company.name like %?2% or j.title like %?2%)")
    Page<Job> findAllByCompanyIdWithKeyword(String companyId, String keyword, Pageable pageable);
}
