package com.openjob.web.company;

import com.openjob.common.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
    Optional<Company> findById(String id);

    @Query("select c from Company c where c.address like %?1%")
    Page<Company> findByLocation(String location, Pageable pageable);

    @Query("select c from Company c where c.name like %?1% or c.description like %?1%")
    Page<Company> findByKeyword(String keyword, Pageable pageable);

    @Query("select c from Company c where (c.name like %?1% or c.description like %?1%) and c.address like %?2%")
    Page<Company> findByKeywordAndLocation(String keyword, String location, Pageable pageable);

    @Query("update Company c set c.accountBalance = c.accountBalance + ?2 where c.id=?1")
    @Modifying
    void updateAccountBalance(String companyId, Double amount);

    @Query("select c from Company c left join User u where u.id = ?1")
    Company findByHeadHunterId(String id);
}
