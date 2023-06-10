package com.openjob.common.model;

import com.openjob.common.enums.SalaryType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalaryModel {
    private Integer min;
    private Integer max;
    private boolean isNegotiable;
    private SalaryType salaryType;
}
