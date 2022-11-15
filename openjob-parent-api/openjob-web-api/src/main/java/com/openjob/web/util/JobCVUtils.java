package com.openjob.web.util;

import com.openjob.common.enums.ExperienceValue;
import com.openjob.common.model.CV;
import com.openjob.common.model.Job;
import com.openjob.common.model.JobSkill;
import com.openjob.common.model.Skill;

import java.util.Objects;

public class JobCVUtils {
    public static int checkCVmatchJob(Job job, CV cv){
        int point = 0;
        for (JobSkill jobSkill : job.getJobSkills()){
            boolean isRequiredSkillMatched = true;
            if (jobSkill.isRequired())
                isRequiredSkillMatched  = false;
            for (Skill skillInCV : cv.getListSkill()) {
                if (checkSkillMatched(jobSkill.getSkill(), skillInCV)){
                    point++;
                    if (jobSkill.isRequired())
                        isRequiredSkillMatched = true;
                    break;
                }
            }
            if ( ! isRequiredSkillMatched)
                return 0;

        }

        return point;
    }

    private static boolean checkSkillMatched(Skill skillInJob, Skill skillInCV) {
        if (Objects.equals(skillInJob.getName(), skillInCV.getName())){
            if (skillInJob.getExperience().name().equals(ExperienceValue.ANY.name()) ||
                skillInJob.getExperience().equals(skillInCV.getExperience()))
                return true;
        }

        return false;
    }


}
