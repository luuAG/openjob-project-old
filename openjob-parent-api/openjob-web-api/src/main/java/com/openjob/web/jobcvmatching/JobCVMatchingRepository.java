//package com.openjob.web.jobcvmatching;
//
//import com.openjob.common.model.JobCvMatching;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface JobCVMatchingRepository extends JpaRepository<JobCvMatching, Integer> {
//
//    @Query("select jc from JobCvMatching jc where jc.job.id=?1 and jc.cv.id=?2 order by jc.point desc")
//    List<JobCvMatching> findByJobIdAndCVId(String jobId, String cvId);
//
//}
