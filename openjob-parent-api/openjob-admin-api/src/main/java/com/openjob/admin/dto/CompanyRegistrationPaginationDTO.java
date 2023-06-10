package com.openjob.admin.dto;

import com.openjob.common.model.CompanyRegistration;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompanyRegistrationPaginationDTO {
    private List<CompanyRegistration> content;
    private int totalPages;
    private long totalElements;

    public CompanyRegistrationPaginationDTO(List<CompanyRegistration> content, int totalPages, long totalElements) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
