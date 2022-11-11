package com.openjob.common.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class JobSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn
    private Job job;

    @ManyToOne
    @JoinColumn
    private Skill skill;

    private boolean isRequired;
}
