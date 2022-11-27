//package com.openjob.common.model;
//
//import com.openjob.common.enums.CvStatus;
//import lombok.Data;
//
//import javax.persistence.*;
//
//@Data
//@Entity
//@Table(name="job_cv_matching")
//public class JobCvMatching {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
//    @ManyToOne
//    @JoinColumn
//    private Job job;
//
//    @ManyToOne
//    @JoinColumn
//    private CV cv;
//
//    private Integer point;
//
//    private CvStatus status;
//}
