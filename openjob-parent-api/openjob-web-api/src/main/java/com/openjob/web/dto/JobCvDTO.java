package com.openjob.web.dto;

import com.openjob.common.enums.CvStatus;
import com.openjob.common.model.CV;
import com.openjob.common.model.Job;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
public class JobCvDTO {
    private Integer id;

    private Job job;

    private CV cv;

    private CvStatus status;

    private Date applyDate;

    private Boolean isMatched;

    private Boolean isApplied;

    private Double point;
}
