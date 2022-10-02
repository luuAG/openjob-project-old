package com.openjob.admin.webuser;

import com.openjob.common.model.WebUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WebUserRepository extends JpaRepository<WebUser, String> {


    @Query("select u from WebUser u " +
            "where u.company.name like '%?1%'")
    Page<WebUser> searchByCompany(String keyword, Pageable pageable);

    @Query("select u from WebUser u " +
            "where concat(u.lastName, ' ', u.firstName, ' ', u.email) like '%?1%'")
    Page<WebUser> searchByKeyword(String keyword, Pageable pageable);
}
