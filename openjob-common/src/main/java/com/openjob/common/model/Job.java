package com.openjob.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openjob.common.enums.JobLevel;
import com.openjob.common.enums.JobType;
import com.openjob.common.enums.WorkPlace;
import com.openjob.common.model.converter.SalaryConverter;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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

    @Convert(converter = SalaryConverter.class)
    private SalaryModel salaryInfo;

    @OneToOne
    @JoinColumn
    private Specialization specialization;

    @OneToOne
    @JoinColumn
    private Major major;

    @OneToOne
    @JoinColumn
    private Company company;

    @OneToMany(mappedBy = "job", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<JobSkill> jobSkills;

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
