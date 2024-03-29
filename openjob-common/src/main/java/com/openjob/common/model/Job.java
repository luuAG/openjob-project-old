package com.openjob.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.openjob.common.enums.JobLevel;
import com.openjob.common.enums.JobStatus;
import com.openjob.common.enums.JobType;
import com.openjob.common.enums.WorkPlace;
import com.openjob.common.model.converter.SalaryConverter;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table
public class Job extends BaseAuditEntity {
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Id
    private String id;

    @Column
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column
    private String hoursPerWeek;

    @Column
    private Integer quantity;

//    @Column
//    private String salary;

//    @Column
//    @Temporal(value = TemporalType.TIMESTAMP)
//    private Date createdAt;

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date expiredAt;

    @Column
    @Enumerated(value = EnumType.STRING)
    private WorkPlace workPlace;

    @Column
    @Enumerated(value = EnumType.STRING)
    private JobLevel jobLevel;

    @Column
    @Enumerated(value = EnumType.STRING)
    private JobType jobType;

//    @Convert(converter = SalaryConverter.class)
    @Embedded
    private SalaryModel salaryInfo;

    @Enumerated(EnumType.STRING)
    private JobStatus jobStatus;

    private double price;

    @OneToOne
    @JoinColumn
//    @JsonIgnore
    private Specialization specialization;

    @OneToOne
    @JoinColumn
//    @JsonIgnore
    private Major major;

    @OneToOne
    @JoinColumn
    @JsonIgnoreProperties({"description", "phone", "address", "totalEmployee", "logoUrl", "imageUrlsString", "contractEndDate",
            "accountBalance", "isActive", "memberType", "companyType", "scope", "email", "headHunter"})
    private Company company;

    @OneToMany(mappedBy = "job", orphanRemoval = true, cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<JobSkill> jobSkills;

    public List<JobSkill> getJobSkills(){
        if (this.jobSkills == null)
            this.jobSkills = new ArrayList<>();
        return this.jobSkills;
    }

    public Job(String id, String title, Date expiredAt,  Specialization specialization, List<JobSkill> jobSkills) {
        this.id = id;
        this.title = title;
        this.expiredAt = expiredAt;
        this.specialization = specialization;
        this.jobSkills =  jobSkills;
    }
    public Job(String id, String title, Date expiredAt,  Specialization specialization, Collection<JobSkill> jobSkills) {
        this.id = id;
        this.title = title;
        this.expiredAt = expiredAt;
        this.specialization = specialization;
        this.jobSkills = (List<JobSkill>) jobSkills;
    }


    public Job() {

    }
}
