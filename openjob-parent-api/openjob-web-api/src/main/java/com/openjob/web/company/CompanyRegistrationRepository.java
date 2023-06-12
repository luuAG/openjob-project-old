package com.openjob.web.company;

import com.openjob.common.model.CompanyRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface CompanyRegistrationRepository extends JpaRepository<CompanyRegistration, String>, JpaSpecificationExecutor<CompanyRegistration> {


}
