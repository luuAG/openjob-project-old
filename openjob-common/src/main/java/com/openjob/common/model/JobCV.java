package com.openjob.common.model;

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

    private Date applyDate;
}
