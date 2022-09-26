package com.openjob.common.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name="hr")
public class HR {
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Id
    private String id;
    @Column(nullable = false, unique = true, length = 20)
    private String username;
    @Column(unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "default 'RECRUITER'")
    private Role role;
    @Column(nullable = false, columnDefinition = "default true")
    private Boolean isActive;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId")
    private Company company;


}
