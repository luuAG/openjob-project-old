package com.openjob.web.dto;

import com.openjob.common.model.Skill;
import lombok.Getter;

import java.util.List;

@Getter
public class CVRequestDTO {
    private String id;
    private String title;
    private String objective;
    private String education;
    private String experience;
    private String certificate;
    private String additionalInfo;
    private String userId;
    private Integer specializationId;
    private Integer majorId;
    private List<Skill> listSkill;

}
