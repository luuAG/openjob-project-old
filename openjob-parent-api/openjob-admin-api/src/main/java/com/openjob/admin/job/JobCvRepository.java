package com.openjob.admin.job;

import com.openjob.common.model.JobCV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JobCvRepository extends JpaRepository<JobCV, Integer> {

    @Query("select jcv from JobCV jcv where jcv.job.id=?1 and jcv.cv.id=?2")
    Optional<JobCV> findByJobIdAndCvId(String jobId, String cvId);
}
