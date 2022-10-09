package com.openjob.common.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table
@Data
public class Major {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @OneToMany
    @JoinColumn(name = "major_id", referencedColumnName = "id")
    private Collection<Specialization> specializations;
}
