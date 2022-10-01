package com.openjob.admin.dto;

import com.openjob.common.model.Company;
import com.openjob.common.model.HR;
import lombok.Data;

@Data
public class CompanyHeadhunterDTO {
    private Company company;
    private HR headHunter;
}
