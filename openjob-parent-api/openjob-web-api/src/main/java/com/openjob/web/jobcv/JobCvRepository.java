package com.openjob.web.jobcv;

import com.openjob.common.model.JobCV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JobCvRepository extends JpaRepository<JobCV, Integer> {

    @Modifying
    @Query("delete from JobCV jc where jc.cv.id=?1 and jc.job.id=?2")
    void deleteByCvIdAndJobId(String cvId, String jobId);
}
