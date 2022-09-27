package com.openjob.common.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;

@MappedSuperclass
public class BaseUser {
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Id
    protected String id;
    @Column(unique = true, nullable = false)
    protected String email;
    @Column(nullable = false)
    @Size(min = 8, max = 32)
    protected String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected Role role;
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


}
