package com.openjob.web.cv;

import com.openjob.common.model.CV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CvRepository extends JpaRepository<CV, String> {

    @Query("select cv from CV cv where cv.user.id=?1")
    Optional<CV> findByUserId(String userId);

    @Query("select cv from CV cv where cv.specialization.id=?1")
    List<CV> findBySpecialization(Integer speId);

}
