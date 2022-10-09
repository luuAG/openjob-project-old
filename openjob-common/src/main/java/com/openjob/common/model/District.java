package com.openjob.common.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "district")
@Data
public class District {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "_name", length = 50)
    private String name;

    @Column(name = "_prefix", length = 20)
    private String prefix;

}
