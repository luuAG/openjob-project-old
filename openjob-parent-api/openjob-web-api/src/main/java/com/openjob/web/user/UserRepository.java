package com.openjob.web.user;

import com.openjob.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("select u from User u join JobCvMatching jcm " +
            "on u.cv.id = jcm.cv.id " +
            "where jcm.job.id=?1")
    List<User> findByMatchingJob(String jobId);
}
