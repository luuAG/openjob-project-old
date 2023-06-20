package com.openjob.web.cv;

import com.openjob.common.model.CvSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CvSkillRepository extends JpaRepository<CvSkill, Integer> {

    @Query("delete from CvSkill cs where cs.cv.id=?1")
    @Modifying
    void deleteByCvId(String cvId);
}
