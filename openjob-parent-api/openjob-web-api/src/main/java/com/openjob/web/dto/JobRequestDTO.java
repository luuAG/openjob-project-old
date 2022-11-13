package com.openjob.web.dto;

import com.openjob.common.enums.WorkPlace;
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
    private Integer specializationId;
    private List<JobSkillDTO> listJobSkillDTO;
    private String companyId;
}
