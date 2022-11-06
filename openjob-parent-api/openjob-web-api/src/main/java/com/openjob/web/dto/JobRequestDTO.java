package com.openjob.web.dto;

import com.openjob.common.enums.WorkPlace;
import com.openjob.common.model.Major;
import com.openjob.common.model.SkillExperience;
import com.openjob.common.model.Specialization;
import lombok.Getter;

import java.util.Collection;

@Getter
public class JobRequestDTO {
    private String title;
    private String description;
    private String hoursPerWeek;
    private Integer quantity;
    private String salary;
    private WorkPlace workPlace;

    private Major major;
    private Specialization specialization;
    private Collection<SkillExperience> listSkillExperience;
    private String companyId;
}
