package com.openjob.admin.major;

import com.openjob.common.model.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MajorRepository extends JpaRepository<Major, Integer> {

    Optional<Major> findByName(String name);
}
