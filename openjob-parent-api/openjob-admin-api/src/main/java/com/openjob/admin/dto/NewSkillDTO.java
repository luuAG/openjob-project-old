package com.openjob.admin.dto;

import com.openjob.common.model.Skill;
import lombok.Getter;

@Getter
public class NewSkillDTO {
    private Skill skill;
    private Integer specializationId;
}
