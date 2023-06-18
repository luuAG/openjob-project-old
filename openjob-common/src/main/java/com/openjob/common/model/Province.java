package com.openjob.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "province")
@Data
public class Province {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "_name", length = 50)
    private String name;

    @Column(name = "_code", length = 20)
    private String code;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "_province_id", referencedColumnName = "id")
    @JsonIgnore
    private Collection<District> districts;

}
