package com.openjob.web.dto;

import com.openjob.common.model.Skill;
import lombok.Getter;

@Getter
public class JobSkillDTO {
    private Skill skill;
    private Boolean isRequired;
}
