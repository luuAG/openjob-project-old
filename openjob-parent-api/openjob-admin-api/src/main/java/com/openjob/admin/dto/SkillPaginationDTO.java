package com.openjob.admin.dto;

import com.openjob.common.model.Skill;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SkillPaginationDTO {
    private List<Skill> content;
    private int totalPages;
    private long totalElements;

    public SkillPaginationDTO(List<Skill> content, int totalPages, long totalElements) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
