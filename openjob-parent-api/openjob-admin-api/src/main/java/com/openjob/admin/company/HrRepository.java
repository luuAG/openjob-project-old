package com.openjob.admin.company;

import com.openjob.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HrRepository extends JpaRepository<User, String> {
    @Query("update User u set u.isActive=true where u.company.id = ?1 and u.role = 'HR'")
    @Modifying
    void activate(String companyId);

    @Query("update User u set u.isActive=false where u.company.id = ?1 and u.role = 'HR'")
    @Modifying
    void deactivate(String companyId);

    @Query("select u from User u where u.company.id = ?1 and u.role = 'HR'")
    Optional<User> findByCompany(String companyId);
}
