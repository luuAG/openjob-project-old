package com.openjob.common.model;

import com.openjob.common.enums.CvStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@Getter
@Setter
public class JobCvTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String jobId;
    private String cvId;
    private CvStatus cvStatus;
    private Date applyDate;
}
