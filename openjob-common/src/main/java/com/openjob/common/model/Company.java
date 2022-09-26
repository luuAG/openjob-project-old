package com.openjob.common.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name="company")
public class Company {
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Id
    private String id;
    @Column(nullable = false)
    private String name;
    @Column
    private String description;
    @Column(nullable = false)
    private String phone;
    @Column
    private String address;
    @Column
    private Integer totalEmployee;
    @Column(nullable = false)
    private String logoURL;
    @Column
    private String wallpaperURL;

    @OneToOne
    @JoinColumn(name = "headHunterId", unique = true, nullable = false)
    private HR headHunter;


}
