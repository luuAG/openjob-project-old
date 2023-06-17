package com.openjob.web.util;

import com.openjob.common.enums.ExperienceValue;
import com.openjob.common.model.*;

import java.util.Objects;

public class JobCVUtils {
    public static double scoreCv(Job job, CV cv){
        double score = 0;
        for (JobSkill jobSkill : job.getJobSkills()){
            for (CvSkill cvSkill : cv.getSkills()) {
                if (Objects.equals(jobSkill.getSkill().getId(), cvSkill.getId())){
                        score += cvSkill.getYoe() * jobSkill.getWeight();
                } else
                    score += cvSkill.getYoe();
            }

        }

        return score;
    }



}
