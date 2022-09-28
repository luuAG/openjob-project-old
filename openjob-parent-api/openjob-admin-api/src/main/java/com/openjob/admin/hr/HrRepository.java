package com.openjob.admin.hr;

import com.openjob.common.model.HR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HrRepository extends JpaRepository<HR, String> {
    @Override
    Optional<HR> findById(String s);
}
