package com.openjob.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openjob.common.enums.WorkPlace;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table
public class Job {
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

    @Column
    private String salary;

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date expiredAt;

    @Column
    @Enumerated(value = EnumType.STRING)
    private WorkPlace workPlace;

    @OneToOne
    @JoinColumn
    private Major major;

    @OneToOne
    @JoinColumn
    private Specialization specialization;

    @OneToOne
    @JoinColumn
    private Company company;

    @OneToMany(mappedBy = "job")
    private List<JobSkill> jobSkills;

    public Job(String id, String title, Date expiredAt, Major major, Specialization specialization, List<JobSkill> jobSkills) {
        this.id = id;
        this.title = title;
        this.expiredAt = expiredAt;
        this.major = major;
        this.specialization = specialization;
        this.jobSkills = jobSkills;
    }

    public Job() {

    }
}
