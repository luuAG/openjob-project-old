package com.openjob.common.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table
@Data
public class Specialization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "specialization")
    private Collection<Skill> skills;

    @ManyToOne
    private Major major;
}
