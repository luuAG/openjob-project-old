package com.openjob.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openjob.common.enums.ExperienceValue;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@Getter
@Setter
public class Skill extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column(columnDefinition = "bit(1) default true")
    private Boolean isVerified;

    @Column(columnDefinition = "varchar(50) default 'ANY'")
    @Enumerated(value = EnumType.STRING)
    private ExperienceValue experience;

    @JsonIgnore
    @ManyToOne
    @JoinColumn
    private Specialization specialization;

    @JsonIgnore
    @OneToMany(mappedBy = "skill")
    private List<JobSkill> jobSkills;


}
