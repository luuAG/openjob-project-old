package com.openjob.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openjob.common.enums.ExperienceValue;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@Data
public class Skill {
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
