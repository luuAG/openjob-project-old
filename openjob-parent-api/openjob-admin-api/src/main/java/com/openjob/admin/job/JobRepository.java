package com.openjob.admin.job;

import com.openjob.common.enums.SalaryType;
import com.openjob.common.model.Job;
import com.openjob.common.model.SalaryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, String>, JpaSpecificationExecutor<Job> {

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

    @Query("update Job j set j.jobStatus = 'APPROVED' where j.id in ?1")
    @Modifying
    void approveByIds(List<String> ids);

    @Query("update Job j set j.jobStatus = 'REJECTED' where j.id in ?1")
    @Modifying
    void rejectByIds(List<String> ids);
}
