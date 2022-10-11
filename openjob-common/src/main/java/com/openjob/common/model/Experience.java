package com.openjob.common.model;

import com.openjob.common.enums.ExperienceValue;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "default 'ANY'")
    @Enumerated(value = EnumType.STRING)
    private ExperienceValue value;
}
