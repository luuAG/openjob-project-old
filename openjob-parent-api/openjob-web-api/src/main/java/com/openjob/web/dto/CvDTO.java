package com.openjob.web.dto;

import com.openjob.common.enums.CvStatus;
import com.openjob.common.model.CvSkill;
import com.openjob.common.model.Major;
import com.openjob.common.model.Skill;
import com.openjob.common.model.Specialization;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CvDTO {
    private String id;
    private String title;

    private String objective;

    private String education;

    private String experience;
    private String certificate;
    private String additionalInfo;

    private String userId;

    private CvStatus status;

    private Specialization specialization;

    private Major major;

    private Double point;

    private Date applyDate;

    private Boolean isMatching;

    private Boolean isApplied;

    private boolean isActive;

    private String cvType;

    private List<CvSkill> skills;
}
