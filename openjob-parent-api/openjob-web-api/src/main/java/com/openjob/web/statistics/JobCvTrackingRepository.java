package com.openjob.web.statistics;

import com.openjob.common.enums.CvStatus;
import com.openjob.common.model.JobCvTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JobCvTrackingRepository extends JpaRepository<JobCvTracking, Integer> {
    @Query("update JobCvTracking jcv set jcv.cvStatus=?3 where jcv.jobId=?1 and jcv.cvId=?2")
    @Modifying
    void updateCvStatus(String jobId, String cvId, CvStatus cvStatus);
}
