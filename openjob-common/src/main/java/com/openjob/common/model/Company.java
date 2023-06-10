package com.openjob.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openjob.common.enums.CompanyType;
import com.openjob.common.enums.MemberType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name="company")
public class Company extends BaseAuditEntity{
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
    private String imageUrls;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date contractEndDate;
    @Column
    private Double accountBalance;
    @Column
    private Boolean isActive;

    @Column
    @Enumerated(EnumType.STRING)
    private MemberType memberType;
    @Column
    @Enumerated(EnumType.STRING)
    private CompanyType companyType;

    @OneToOne
    @JoinColumn
    @JsonIgnore
    private User headHunter;

    @Transient
    private String[] base64Images;

    public List<String> getImageUrls(){
        String[] urls = this.imageUrls.split(", ");
        return Arrays.stream(urls).collect(Collectors.toList());
    }
    public void setImageUrls(List<String> urls) {
        this.imageUrls = "";
        urls.forEach(url -> this.imageUrls += url + ", ");
    }

}
