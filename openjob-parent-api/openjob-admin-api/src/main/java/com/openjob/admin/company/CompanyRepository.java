package com.openjob.admin.company;

import com.openjob.common.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {

    @Query("select c from Company c where c.name like '%?1%'")
    Page<Company> findByKeyword(String keyword, Pageable pageable);
}
