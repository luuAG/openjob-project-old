package com.openjob.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BaseAuditDTO {
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
