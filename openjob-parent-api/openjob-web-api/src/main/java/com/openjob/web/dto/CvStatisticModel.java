package com.openjob.web.dto;

import com.openjob.common.enums.CvStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class CvStatisticModel {
    private String companyId;
    private String companyName;

    private String jobId;
    private String jobTitle;
    private Date jobCreatedAt;

    private String cvId;
    private CvStatus cvStatus;
    private Date applyDate;
}
