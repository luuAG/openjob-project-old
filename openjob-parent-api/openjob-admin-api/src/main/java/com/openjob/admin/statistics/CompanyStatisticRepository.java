package com.openjob.admin.statistics;

import com.openjob.common.model.CompanyStatistic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyStatisticRepository extends JpaRepository<CompanyStatistic, Integer> {

     @Query("select new com.openjob.admin.statistics.CvCompanyDTO(c.companyName, count(j.id))  " +
             "from CompanyStatistic c join JobCvTracking j " +
             "on c.jobId=j.jobId " +
             "group by c.companyName " +
             "order by count(j.id) desc")
     List<CvCompanyDTO> findTop10MostCvAppliedCompanies(Pageable pageable);

     @Query("select new com.openjob.admin.statistics.CvCompanyDTO(c.companyName, count(j.id)) " +
             "from CompanyStatistic c join JobCvTracking j " +
             "on c.jobId=j.jobId " +
             "where c.companyId=?1 " +
             "group by c.companyName")
     List<CvCompanyDTO> findCvCompanyStatisticByCompanyId(String companyId);
}
