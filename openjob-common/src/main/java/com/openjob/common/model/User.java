package com.openjob.common.model;

import com.openjob.common.enums.AuthProvider;
import com.openjob.common.enums.Role;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
@Entity
@Table
public class User {
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Id
    private String id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(10) default 'DATABASE'")
    private AuthProvider authProvider;
    @Column(columnDefinition = "bit(1) default true")
    private Boolean isActive;
    @Column
    @Size(max = 20)
    private String firstName;
    @Column
    @Size(max = 20)
    private String lastName;
    @Column
    private String avatarUrl;
    @Column
    private String dob;
    @Column
    private String gender;
    @Column
    private Integer reports;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn
    private Company company;

    @OneToOne
    private CV cv;

}
