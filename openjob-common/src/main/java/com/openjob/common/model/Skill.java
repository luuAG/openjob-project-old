package com.openjob.common.model;

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

    @Column
    private Boolean isVerified;

}
