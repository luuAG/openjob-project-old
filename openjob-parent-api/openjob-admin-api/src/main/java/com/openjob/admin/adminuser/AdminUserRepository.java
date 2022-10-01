package com.openjob.admin.adminuser;

import com.openjob.common.model.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<Admin, String> {

    @Query("select a " +
            "from Admin a where concat(a.username, ' ', a.firstName, ' ', a.lastName) like %:keyword% ")
    Page<Admin> searchAllByPage(@Param("keyword") String keyword, Pageable pageable);

    @Query("select a " +
            "from Admin a where concat(a.username, ' ', a.firstName, ' ', a.lastName) like %:keyword% " +
            "and a.isActive = :isActive")
    Page<Admin> searchActiveByPage(@Param("keyword") String keyword,
                                @Param("isActive") Boolean isActive,
                                Pageable pageable);

    @Query("select a from Admin a where a.username like ?1")
    Optional<Admin> findByUsername(String username);
}
