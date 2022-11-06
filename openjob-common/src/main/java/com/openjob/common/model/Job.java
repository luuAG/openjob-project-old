package com.openjob.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openjob.common.enums.WorkPlace;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

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

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="job_skill_experience",
            joinColumns = @JoinColumn( name="job_id"),
            inverseJoinColumns = @JoinColumn( name="skill_experience_id")
    )
    private Collection<SkillExperience> listSkillExperience;

    @OneToOne
    @JoinColumn
    private Company company;

    public Job(String id, String title, Date expiredAt, Major major, Specialization specialization, Collection<SkillExperience> listSkillExperience) {
        this.id = id;
        this.title = title;
        this.expiredAt = expiredAt;
        this.major = major;
        this.specialization = specialization;
        this.listSkillExperience = listSkillExperience;
    }

    public Job() {

    }
}
