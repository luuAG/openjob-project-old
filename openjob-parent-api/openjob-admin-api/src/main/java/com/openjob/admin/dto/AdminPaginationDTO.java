package com.openjob.admin.dto;

import com.openjob.common.model.Admin;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

@Getter
public class AdminPaginationDTO {
    private Collection<Admin> admins;
    private Integer totalPages;
    private Long totalElements;

    public AdminPaginationDTO(List<Admin> content, int totalPages, long totalElements) {
        this.admins = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
