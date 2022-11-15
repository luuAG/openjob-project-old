package com.openjob.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillDTO {
    private Integer id;
    private Boolean isVerified;
    private String name;
    private String specialization;
    private String major;
}
