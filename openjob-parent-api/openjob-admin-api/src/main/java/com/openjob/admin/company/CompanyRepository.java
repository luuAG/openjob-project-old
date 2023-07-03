package com.openjob.admin.company;

import com.openjob.common.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String>, JpaSpecificationExecutor<Company> {

    @Query("select c from Company c where c.name like %?1%")
    Page<Company> findByKeyword(String keyword, Pageable pageable);


    Optional<Company> findByName(String name);

    @Query("delete from CompanyRegistration cr where cr.id in :ids")
    @Modifying
    void rejectManyCompaniesByIds(@Param("ids") List<String> ids);

    @Query("update Company c set c.amountOfFreeJobs=?1, c.amountOfFreeCvViews=?2 " +
            "where c.memberType=com.openjob.common.enums.MemberType.DEFAULT")
    @Modifying
    void resetFreeServiceForAll(Integer freeJob, Integer freeCvView);

    @Query("update Company c set c.amountOfFreeJobs=?1, c.amountOfFreeCvViews=?2 " +
            "where c.memberType=com.openjob.common.enums.MemberType.PREMIUM")
    @Modifying
    void resetPremiumServiceForAll(Integer premiumFreeJob, Integer premiumFreeViewCv);

    @Query("select c.isActive from Company c where c.id=?1")
    boolean checkActiveById(String id);
}
