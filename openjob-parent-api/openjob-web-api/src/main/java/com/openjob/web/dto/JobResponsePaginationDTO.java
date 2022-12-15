package com.openjob.web.dto;

import lombok.Getter;

import java.util.Collection;

@Getter
public class JobResponsePaginationDTO {
    Collection<JobResponseDTO> listJob;
    Integer totalPages;
    Long totalElements;

    public JobResponsePaginationDTO(Collection<JobResponseDTO> listJob, Integer totalPages, Long totalElements) {
        this.listJob = listJob;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
