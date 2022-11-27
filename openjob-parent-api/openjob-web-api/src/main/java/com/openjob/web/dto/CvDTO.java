package com.openjob.web.dto;

import com.openjob.common.enums.CvStatus;
import com.openjob.common.model.Major;
import com.openjob.common.model.Skill;
import com.openjob.common.model.Specialization;
import lombok.Getter;
import lombok.Setter;

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

    private List<Skill> listSkill;
}
