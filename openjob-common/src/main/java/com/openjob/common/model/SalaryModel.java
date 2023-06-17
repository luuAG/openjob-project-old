package com.openjob.common.model;

import com.openjob.common.enums.SalaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class SalaryModel {
    private Integer minSalary;
    private Integer maxSalary;
    private Boolean isSalaryNegotiable;
    @Enumerated(EnumType.STRING)
    private SalaryType salaryType;
}
