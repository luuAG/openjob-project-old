package com.openjob.common.model;

import com.openjob.common.enums.ServiceType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
public class Invoice extends BaseAuditEntity {
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Id
    private String id;

    private String companyId;
    private String companyName;
    private double amount;
    private ServiceType serviceType;
}
