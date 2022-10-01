package com.openjob.admin.dto;

import com.openjob.common.model.Company;
import com.openjob.common.model.HR;
import lombok.Getter;

@Getter
public class CompanyHeadhunterResponseDTO {

    private  Company company;
    private  HR headHunter;

    public CompanyHeadhunterResponseDTO(HR savedHr, Company company) {
        this.company = company;
        this.headHunter = savedHr;
    }
}
