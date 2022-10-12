package com.openjob.admin.dto;

import com.openjob.common.model.Job;

import java.util.Collection;
import java.util.List;

public class JobPaginationDTO {
    private Collection<Job> jobs;
    private Integer totalPages;
    private Long totalElements;

    public JobPaginationDTO(List<Job> jobs, Integer totalPages, Long totalElements) {
        this.jobs = jobs;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
