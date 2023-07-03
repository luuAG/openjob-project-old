package com.openjob.admin.dto;

import lombok.Getter;

@Getter
public class DeactivateJobDTO {
    private String jobId;
    private String reason;
}
