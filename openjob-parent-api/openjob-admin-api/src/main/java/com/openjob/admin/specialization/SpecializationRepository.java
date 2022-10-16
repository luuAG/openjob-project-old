package com.openjob.admin.specialization;

import com.openjob.common.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Integer> {

    Optional<Specialization> findByName(String name);

    @Query("select s from Specialization s where s.major.id = ?1")
    Collection<Specialization> findByMajor(Integer majorId);
}
