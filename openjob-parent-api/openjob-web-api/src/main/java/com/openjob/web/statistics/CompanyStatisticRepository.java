package com.openjob.web.statistics;

import com.openjob.common.model.CompanyStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyStatisticRepository extends JpaRepository<CompanyStatistic, Integer> {
}
