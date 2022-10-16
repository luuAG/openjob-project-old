package com.openjob.admin.webuser;

import com.openjob.common.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WebUserRepository extends JpaRepository<User, String> {



    @Query("select u from User u " +
            "where concat(u.lastName, ' ', u.firstName, ' ', u.email) like %?1% " +
            "and u.role = 'USER'")
    Page<User> searchByKeyword(String keyword, Pageable pageable);
}
