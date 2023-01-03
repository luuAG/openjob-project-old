package com.openjob.web.dto;

import com.openjob.common.model.JobCV;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class JobCvPaginationDTO {
    Collection<JobCV> listJobCv;
    Integer totalPages;
    Long totalElements;

    public JobCvPaginationDTO(Collection<JobCV> listJobCv, Integer totalPages, Long totalElements) {
        this.listJobCv = listJobCv;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
