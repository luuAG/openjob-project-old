package com.openjob.web.dto;

import com.openjob.common.model.CvSkill;
import com.openjob.common.model.Skill;
import com.openjob.web.user.UserService;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
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
    private boolean isActive;
    private List<CvSkill> listSkill;


}
