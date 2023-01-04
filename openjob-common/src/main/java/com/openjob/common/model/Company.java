package com.openjob.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="company")
public class Company {
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Id
    private String id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(columnDefinition = "text")
    private String description;
    @Column
    private String phone;
    @Column
    private String address;
    @Column
    private Integer totalEmployee;
    @Column
    private String logoUrl;
    @Column
    private String wallpaperUrl;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date contractEndDate;
    @Column
    private Double accountBalance;
    @Column
    private Boolean isActive;

    @OneToOne
    @JoinColumn
    @JsonIgnore
    private User headHunter;


}
