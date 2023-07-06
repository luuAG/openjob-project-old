package com.openjob.admin.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CvCompanyDTO {
    private String companyName;
    private Long amountOfCV;
}
