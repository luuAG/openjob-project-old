package com.openjob.web.specialization;

import com.openjob.common.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Integer> {

    @Query("select s from Specialization s where s.major.id=?1")
    List<Specialization> findByMajor(Integer majorId);
}
