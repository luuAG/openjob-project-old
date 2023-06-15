package com.openjob.web.job;

import com.openjob.common.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface JobRepository extends JpaRepository<Job, String>, JpaSpecificationExecutor<Job> {

    @Query("select distinct j from Job j join j.jobSkills js " +
            "where concat(j.title, ' ', j.company.name, ' ', js.skill.name) like %?1% ")
    Page<Job> findByKeyword(String keyword, Pageable pageable);

    @Query("select distinct j from Job j where j.company.address like %?1%")
    Page<Job> findByLocation(String location, Pageable pageable);

    @Query("select distinct j from Job j join j.jobSkills js " +
            "where concat(j.title, ' ', j.company.name, ' ', js.skill.name) like %?1% " +
            "and j.company.address like %?2%")
    Page<Job> findByKeywordAndLocation(String keyword, String location, Pageable pageable);

    @Override
    void deleteById(String s);

    @Query("select j from Job j where j.specialization.id=?1")
    List<Job> findBySpecialization(Integer id);

    @Query("select j from Job j where j.company.id=?1")
    Page<Job> findByCompanyId(String cId, Pageable pageable);

    @Query("select j from Job j where current_date() > j.expiredAt")
    List<Job> findExpiredJob();

    @Query("select j from Job j where j.specialization.id=?1")
    List<Job> findBySpecialization(Integer id, Pageable pageable);

    @Query("select j from Job j join j.jobSkills js where js.skill.id in ?1")
    Page<Job> findBySkillIds(Set<Integer> skillIds, Pageable pageable);
}
