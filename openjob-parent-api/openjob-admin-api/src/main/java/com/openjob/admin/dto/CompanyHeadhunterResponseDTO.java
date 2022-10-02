package com.openjob.admin.dto;

import lombok.Getter;

@Getter
public class CompanyHeadhunterResponseDTO {

    private String companyId;
    private String headHunterId;

    public CompanyHeadhunterResponseDTO(String companyId, String headHunterId) {
        this.companyId = companyId;
        this.headHunterId = headHunterId;
    }
}
