package com.openjob.admin.dto;

import com.openjob.common.model.WebUser;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

@Getter
public class UserPaginationDTO {
    private Collection<WebUser> users;
    private Integer totalPages;
    private Long totalElements;
    public UserPaginationDTO(List<WebUser> content, int totalPages, long totalElements) {
        this.users = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
