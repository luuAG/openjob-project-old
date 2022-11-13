package com.openjob.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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

    @OneToMany
    @JoinTable(
        name = "cv_skill",
        joinColumns = @JoinColumn(name = "cv_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> listSkill;
}
