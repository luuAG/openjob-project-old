package com.openjob.common.model;

import com.openjob.common.enums.CvStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "job_id", "cv_id" }) })
@Getter
@Setter
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
    private Boolean isMatched;

    private Boolean isApplied;

    private Double point;
}
