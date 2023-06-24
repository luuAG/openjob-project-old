package com.openjob.web.job;

import com.openjob.common.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface JobRepository extends JpaRepository<Job, String>, JpaSpecificationExecutor<Job> {

    @Override
    void deleteById(String s);


    @Query("select j from Job j where current_date() > j.expiredAt")
    List<Job> findExpiredJob();

    @Query("select j from Job j where current_date() > j.expiredAt and j.jobStatus <> com.openjob.common.enums.JobStatus.HIDDEN")
    List<Job> findUnhiddenExpiredJob();

    @Query("select j from Job j where j.specialization.id=?1 and j.jobStatus = com.openjob.common.enums.JobStatus.APPROVED")
    List<Job> findBySpecialization(Integer id, Pageable pageable);

    @Query("select j from Job j join j.jobSkills js where js.skill.id in ?1 and j.jobStatus = com.openjob.common.enums.JobStatus.APPROVED")
    Page<Job> findBySkillIds(Set<Integer> skillIds, Pageable pageable);

}
