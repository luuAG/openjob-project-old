package com.openjob.web.jobcv;

import com.openjob.common.model.JobCV;
import com.openjob.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobCvRepository extends JpaRepository<JobCV, Integer>, JpaSpecificationExecutor<JobCV> {

    @Modifying
    @Query("delete from JobCV jc where jc.cv.id=?1 and jc.job.id=?2")
    void deleteByCvIdAndJobId(String cvId, String jobId);

    @Query("select jc from JobCV jc where jc.job.id=?1 and jc.cv.id=?2")
    Optional<JobCV> findByJobIdAndCvId(String jobId, String cvId);

    @Query("select jc from JobCV jc where jc.cv.user.id=?1 and jc.job.id=?2")
    Optional<JobCV> findByUserIdAndJobId(String userId, String jobId);

    @Query("delete from JobCV jcv where jcv.job.id=?1")
    @Modifying
    void deleteByJobId(String jobId);

    @Query("select jcv.cv.user from JobCV jcv where jcv.job.id=?1 and jcv.isApplied=true")
    List<User> findUserAppliedJob(String jobId);

    @Query("select jcv from JobCV jcv where jcv.job.id=?1 and jcv.isApplied=true")
    List<JobCV> findByJobId(String jobId);
}
