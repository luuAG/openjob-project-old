package com.openjob.web.cv;

import com.openjob.common.model.CV;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CvRepository extends JpaRepository<CV, String>, JpaSpecificationExecutor<CV> {

    @Query("select cv from CV cv where cv.user.id=?1")
    Optional<CV> findByUserId(String userId);

    @Query("select case when count(cc)>0 then true else false end from CvCompany cc where cc.cv.id=?1 and cc.company.id=?2")
    boolean checkCompanyChargedToViewCv(String cvId, String companyId);
}
