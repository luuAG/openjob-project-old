package com.openjob.common.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name="web_user")
public class WebUser {
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Id
    private String id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    @Size(min = 8, max = 32)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "default 'CANDIDATE'", nullable = false)
    private Role role;
    @Column(nullable = false, columnDefinition = "default true")
    private Boolean isActive;
    @Column(nullable = false)
    @Size(max = 20)
    private String firstName;
    @Column(nullable = false)
    @Size(max = 20)
    private String lastName;
    @Column
    private String avatarURL;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId")
    private Company company;


}
