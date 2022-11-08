package com.openjob.web.job;

import com.openjob.common.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {

    @Query("select distinct j from Job j join j.listSkillExperience se " +
            "where concat(j.title, ' ', j.company.name, ' ', se.skill.name) like %?1% ")
    Page<Job> findByKeyword(String keyword, Pageable pageable);

    @Query("select distinct j from Job j where j.company.address like %?1%")
    Page<Job> findByLocation(String location, Pageable pageable);

    @Query("select distinct j from Job j join j.listSkillExperience se " +
            "where concat(j.title, ' ', j.company.name, ' ', se.skill.name) like %?1% " +
            "and j.company.address like %?2%")
    Page<Job> findByKeywordAndLocation(String keyword, String location, Pageable pageable);

    @Override
    void deleteById(String s);
}
