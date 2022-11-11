package com.openjob.web.jobcvmatching;

import com.openjob.common.model.JobCvMatching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobCVMatchingRepository extends JpaRepository<JobCvMatching, Integer> {
}
