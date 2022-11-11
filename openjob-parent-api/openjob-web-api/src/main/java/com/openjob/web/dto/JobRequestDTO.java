package com.openjob.web.dto;

import com.openjob.common.enums.WorkPlace;
import com.openjob.common.model.JobSkill;
import lombok.Getter;

import java.util.List;

@Getter
public class JobRequestDTO {
    private String title;
    private String description;
    private String hoursPerWeek;
    private Integer quantity;
    private String salary;
    private WorkPlace workPlace;

    private Integer majorId;
    private Integer specializationId;
    private List<JobSkill> listJobSkill;
    private String companyId;
}
