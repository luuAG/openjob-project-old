package com.openjob.common.model;

import com.openjob.common.enums.CvStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "job_id", "cv_id" }) })
@Data
public class JobCV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn
    private Job job;

    @ManyToOne
    @JoinColumn
    private CV cv;

    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(10) default 'NEW'")
    private CvStatus status;

    private Date applyDate;

    @Column(columnDefinition = "bit(1) default false")
    private Boolean isMatching;

    private Boolean isApplied;

    private Double point;
}
