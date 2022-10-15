package com.openjob.admin.dto;

import com.openjob.common.model.Specialization;
import lombok.Getter;

@Getter
public class NewSpecializationDTO {
    private Specialization specialization;
    private Integer majorId;
}
