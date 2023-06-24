package com.openjob.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cv")
@Data
public class CV {
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Id
    private String id;

    private String title;

    @Column(columnDefinition = "text")
    private String objective;

    @Column(columnDefinition = "text")
    private String education;

    @Column(columnDefinition = "text")
    private String experience;

    @Column(columnDefinition = "text")
    private String certificate;

    private String additionalInfo;

    @OneToOne
    @JoinColumn
    @JsonIgnore
    private User user;

    @OneToOne
    @JoinColumn
    private Specialization specialization;

    @OneToOne
    @JoinColumn
    private Major major;

    private boolean isActive;

//    @OneToMany(orphanRemoval = true)
//    @JoinTable(
//        name = "cv_skill",
//        joinColumns = @JoinColumn(name = "cv_id"),
//        inverseJoinColumns = @JoinColumn(name = "skill_id")
//    )
    @OneToMany(mappedBy = "cv", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CvSkill> skills;

    public List<CvSkill> getSkills(){
        if (this.skills == null)
            this.skills = new ArrayList<>();
        return this.skills;
    }
}
