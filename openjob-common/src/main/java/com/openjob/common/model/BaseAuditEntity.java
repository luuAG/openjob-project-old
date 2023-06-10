package com.openjob.common.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseAuditEntity {
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    private String updatedBy;
}
