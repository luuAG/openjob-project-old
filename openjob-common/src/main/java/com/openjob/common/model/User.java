package com.openjob.common.model;

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
    protected String id;
    @Column(unique = true, nullable = false)
    protected String email;
    @Column(nullable = false)
    protected String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected Role role;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(10) default 'DATABASE'")
    protected AuthProvider authProvider;
    @Column(nullable = false, columnDefinition = "bit(1) default true")
    protected Boolean isActive;
    @Column(nullable = false)
    @Size(max = 20)
    protected String firstName;
    @Column(nullable = false)
    @Size(max = 20)
    protected String lastName;
    @Column
    protected String avatarUrl;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn
    private Company company;


}
