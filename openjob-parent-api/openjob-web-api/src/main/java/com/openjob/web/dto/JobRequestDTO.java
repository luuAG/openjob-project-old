package com.openjob.web.dto;

import com.openjob.common.enums.JobLevel;
import com.openjob.common.enums.JobStatus;
import com.openjob.common.enums.JobType;
import com.openjob.common.enums.WorkPlace;
import com.openjob.common.model.SalaryModel;
import com.openjob.common.model.converter.SalaryConverter;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
import java.util.List;

@Getter
public class JobRequestDTO {
    private String id;
    private String title;
    private String description;
    private String hoursPerWeek;
    private Integer quantity;
    private Date expiredAt;
    private WorkPlace workPlace;
    private JobLevel jobLevel;
    private JobType jobType;
    private SalaryModel salaryInfo;
    private Integer specializationId;
    private List<JobSkillDTO> listJobSkillDTO;
    private String companyId;
    private JobStatus jobStatus;
    private double jobPrice;
}
