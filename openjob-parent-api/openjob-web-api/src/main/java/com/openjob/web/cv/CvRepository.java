package com.openjob.web.cv;

import com.openjob.common.model.CV;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CvRepository extends JpaRepository<CV, String>, JpaSpecificationExecutor<CV> {

    @Query("select cv from CV cv where cv.user.id=?1")
    Optional<CV> findByUserId(String userId);

    @Query("select cv from CV cv where cv.specialization.id=?1")
    List<CV> findBySpecialization(Integer speId);

    @Query("select cv from JobCV jcv join CV cv on jcv.cv.id=cv.id where jcv.job.id=?1")
    Page<CV> findByJobId(String jobId, Pageable pageable);

    @Query("select cv from JobCV jcv join CV cv on jcv.cv.id=cv.id where jcv.job.id=?1 and jcv.isApplied=true")
    Page<CV> findCvAppliedByJobId(String jobId, Pageable pageable);
}
