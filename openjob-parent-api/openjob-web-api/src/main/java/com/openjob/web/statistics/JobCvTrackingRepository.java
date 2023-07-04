package com.openjob.web.statistics;

import com.openjob.common.model.JobCvTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobCvTrackingRepository extends JpaRepository<JobCvTracking, Integer> {
}
