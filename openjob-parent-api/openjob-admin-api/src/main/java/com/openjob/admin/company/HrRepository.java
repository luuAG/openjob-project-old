package com.openjob.admin.company;

import com.openjob.common.model.HR;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HrRepository extends JpaRepository<HR, String> {
    @Override
    Optional<HR> findById(String s);

    @Query("select hr from HR hr where hr.email=?1")
    Optional<HR> findByEmail(String email);

    @Override
    Page<HR> findAll(Pageable pageable);

    @Query("select hr from HR hr where concat(hr.email, ' ', hr.firstName, ' ', hr.lastName) like '%?1%'")
    Page<HR> search(String keyword, Pageable pageable);

    @Query("select hr from HR hr where hr.company.name like '%?1%'")
    Page<HR> searchByCompany(String keyword, Pageable pageable);

    @Query("select hr from HR hr where hr.company.id like '?1'")
    Page<HR> findByCompanyId(String companyId, Pageable pageable);
}
