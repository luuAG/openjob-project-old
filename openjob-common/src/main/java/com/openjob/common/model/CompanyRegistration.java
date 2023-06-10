package com.openjob.common.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "company_registration")
public class CompanyRegistration extends BaseAuditEntity{
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Id
    private String id;

    private String companyName;
    private String email;
    private String headHunterName;
    private String phone;
    private String position;
}
