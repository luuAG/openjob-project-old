package com.openjob.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

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

    @JsonIgnore
    @ManyToOne
    @JoinColumn
    private Specialization specialization;

}
