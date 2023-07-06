package com.openjob.web.statistics;

import com.openjob.common.enums.CvStatus;
import com.openjob.common.model.JobCvTracking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface JobCvTrackingRepository extends JpaRepository<JobCvTracking, Integer> {
    @Query("update JobCvTracking jcv set jcv.cvStatus=?3 where jcv.jobId=?1 and jcv.cvId=?2")
    @Modifying
    void updateCvStatus(String jobId, String cvId, CvStatus cvStatus);


    @Query("SELECT new com.openjob.web.statistics.CvStatisticDTO(c.jobTitle, c.jobCreatedAt, " +
            "    COUNT(j.cvId), " +
            "    SUM(CASE WHEN j.cvStatus = com.openjob.common.enums.CvStatus.ACCEPTED THEN 1 ELSE 0 END), " +
            "    SUM(CASE WHEN j.cvStatus = com.openjob.common.enums.CvStatus.REJECTED THEN 1 ELSE 0 END)) " +
            "FROM CompanyStatistic c join JobCvTracking j on c.jobId = j.jobId " +
            "WHERE c.companyId = ?1 " +
            "GROUP BY c.jobTitle, c.jobCreatedAt")
    List<CvStatisticDTO> findCvStatistic(String companyId, Pageable pageable);

    @Query("SELECT new com.openjob.web.statistics.CvStatisticDTO(c.jobTitle, c.jobCreatedAt, " +
            "    COUNT(j.cvId), " +
            "    SUM(CASE WHEN j.cvStatus = com.openjob.common.enums.CvStatus.ACCEPTED THEN 1 ELSE 0 END), " +
            "    SUM(CASE WHEN j.cvStatus = com.openjob.common.enums.CvStatus.REJECTED THEN 1 ELSE 0 END)) " +
            "FROM CompanyStatistic c join JobCvTracking j on c.jobId = j.jobId " +
            "WHERE c.companyId = ?1 AND c.jobCreatedAt >= ?2 AND c.jobCreatedAt <= ?3 " +
            "GROUP BY c.jobTitle, c.jobCreatedAt")
    List<CvStatisticDTO> findCvStatisticWithDateRange(String companyId, Date startDate, Date endDate, Pageable pageable);
}
