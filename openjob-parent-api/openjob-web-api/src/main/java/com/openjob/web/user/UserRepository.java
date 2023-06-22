package com.openjob.web.user;

import com.openjob.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

//    @Query("select new com.openjob.web.dto.UserCvDto(u.id, jcm.cv.id, u.firstName, u.lastName, u.email, u.phone, u.gender, jcm.point, jcm.status) from User u join JobCV jcm " +
//            "on u.cv.id = jcm.cv.id " +
//            "where jcm.job.id=?1 and jcm.isMatching=true")
//    List<UserCvDto> findByMatchingJob(String jobId);
//
//    @Query("select new com.openjob.web.dto.UserCvDto(u.id, jcm.cv.id, u.firstName, u.lastName, u.email, u.phone, u.gender, jcm.point, jcm.status) from User u join JobCV jcm " +
//            "on u.cv.id = jcm.cv.id " +
//            "where jcm.job.id=?1 and jcm.isApplied=true")
//    List<UserCvDto> findAppliedJob(String jobId);
}
