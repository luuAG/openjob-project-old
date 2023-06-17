package com.openjob.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class CvSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn
    @JsonIgnore
    private CV cv;

    @ManyToOne
    @JoinColumn
    private Skill skill;

    private double yoe;
}
