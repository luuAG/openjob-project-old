package com.openjob.admin.dto;

import com.openjob.common.model.Job;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class ReviewJobDTO {
    private List<Job> jobs;
    private List<String> rejectReasons;
}
