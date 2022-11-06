package com.openjob.web.dto;

import com.openjob.common.model.Job;
import lombok.Getter;

import java.util.Collection;

@Getter
public class JobPaginationDTO {
    Collection<Job> listJob;
    Integer totalPages;
    Long totalElements;

    public JobPaginationDTO(Collection<Job> listJob, Integer totalPages, Long totalElements) {
        this.listJob = listJob;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
