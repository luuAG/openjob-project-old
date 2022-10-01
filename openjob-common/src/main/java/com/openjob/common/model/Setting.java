package com.openjob.common.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Data
@Entity
public class Setting {
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    @Column(columnDefinition = "CHAR(32)")
    @Id
    protected String id;

    @Column(unique = true, nullable = false)
    @Size(max = 32)
    private String name;

    @Column(unique = true, nullable = false, columnDefinition = "text")
    private String value;
}
