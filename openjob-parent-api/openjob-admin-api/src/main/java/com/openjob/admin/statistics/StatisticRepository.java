package com.openjob.admin.statistics;

import com.openjob.common.model.CompanyStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticRepository extends JpaRepository<CompanyStatistic, Integer> {
}
