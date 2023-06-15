package com.openjob.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class JobResponsePaginationDTO {
    Collection<JobResponseDTO> listJob;
    Collection<JobResponseDTO> listRelevantJob;
    Integer totalPages;
    Long totalElements;

    public JobResponsePaginationDTO(Collection<JobResponseDTO> listJob, Integer totalPages, Long totalElements) {
        this.listJob = listJob;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public JobResponsePaginationDTO(Collection<JobResponseDTO> listJob, Collection<JobResponseDTO> listRelevantJob, Integer totalPages, Long totalElements) {
        this.listJob = listJob;
        this.listRelevantJob = listRelevantJob;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
