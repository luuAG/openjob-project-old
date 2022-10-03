package com.openjob.admin.company;

import com.openjob.common.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HrRepository extends JpaRepository<User, String> {

    @Query("select u from User u " +
            "where u.email=?1 and (u.role = 'HEAD_HUNTER' or u.role = 'RECRUITER')")
    Optional<User> findByEmail(String email);

    @Query("select u from User u " +
            "where u.role = 'HEAD_HUNTER' or u.role = 'RECRUITER'")
    Page<User> findAll(Pageable pageable);

    @Query("select u from User u " +
            "where concat(u.email, ' ', u.firstName, ' ', u.lastName) like '%?1%' " +
            "and (u.role = 'HEAD_HUNTER' or u.role = 'RECRUITER')")
    Page<User> search(String keyword, Pageable pageable);

    @Query("select u from User u " +
            "where u.company.name like '%?1%' " +
            "and (u.role = 'HEAD_HUNTER' or u.role = 'RECRUITER')")
    Page<User> searchByCompany(String keyword, Pageable pageable);

    @Query("select u from User u where u.company.id like '?1' " +
            "and (u.role = 'HEAD_HUNTER' or u.role = 'RECRUITER')")
    Page<User> findByCompanyId(String companyId, Pageable pageable);
}
