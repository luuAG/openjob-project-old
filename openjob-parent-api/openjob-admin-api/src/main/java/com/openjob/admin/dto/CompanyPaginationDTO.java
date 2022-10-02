package com.openjob.admin.dto;

import com.openjob.common.model.Company;
import lombok.Getter;

import java.util.Collection;

@Getter
public class CompanyPaginationDTO {
    private Collection<Company> companies;
    private Integer totalPages;
    private Long totalElements;

    public CompanyPaginationDTO(Collection<Company> companies, Integer totalPages, Long totalElements) {
        this.companies = companies;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
