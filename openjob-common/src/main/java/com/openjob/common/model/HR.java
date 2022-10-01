package com.openjob.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name="hr")
public class HR extends BaseUser {

    @ApiModelProperty(hidden = true)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Company company;


}
