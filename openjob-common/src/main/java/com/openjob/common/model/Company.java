package com.openjob.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openjob.common.enums.CompanyType;
import com.openjob.common.enums.MemberType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Primary;

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
    @Column(length = 500)
    private String imageUrlsString;
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

    @Column
    private Integer scope;

    @Column
    private String email;

    @OneToOne
    @JoinColumn
    @JsonIgnore
    private User headHunter;

    @Transient
    private String[] base64Images;
    @Transient
    private List<String> imageUrls;



    public void setImageUrlsStringCustom(List<String> urls) {
        if (StringUtils.isEmpty(this.imageUrlsString))
            this.imageUrlsString = "";
        urls.forEach(url -> this.imageUrlsString += url + ", ");
    }
    public void initializeImageUrls(){
        if (!StringUtils.isEmpty(this.imageUrlsString)){
            String[] urls = this.imageUrlsString.split(", ");
            imageUrls = Arrays.stream(urls).collect(Collectors.toList());
        }
    }

}
