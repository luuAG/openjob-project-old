package com.openjob.web.cv;

import com.openjob.common.model.CvCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CvCompanyRepository extends JpaRepository<CvCompany, Integer> {
}
