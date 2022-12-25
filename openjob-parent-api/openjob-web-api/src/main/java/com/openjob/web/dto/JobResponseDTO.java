package com.openjob.web.dto;

import com.openjob.common.enums.WorkPlace;
import com.openjob.common.model.Company;
import com.openjob.common.model.JobSkill;
import com.openjob.common.model.Major;
import com.openjob.common.model.Specialization;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class JobResponseDTO {
    private String id;
    private String title;
    private String description;
    private String hoursPerWeek;
    private Integer quantity;
    private String salary;
    private Date createdAt;
    private Date expiredAt;
    private WorkPlace workPlace;
    private Specialization specialization;
    private Major major;
    private Company company;
    private List<JobSkill> jobSkills;
    private Boolean isApplied;
}
