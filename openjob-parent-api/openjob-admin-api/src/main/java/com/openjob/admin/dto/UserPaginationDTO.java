package com.openjob.admin.dto;

import com.openjob.common.model.User;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

@Getter
public class UserPaginationDTO {
    private Collection<User> users;
    private Integer totalPages;
    private Long totalElements;
    public UserPaginationDTO(List<User> content, int totalPages, long totalElements) {
        this.users = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
