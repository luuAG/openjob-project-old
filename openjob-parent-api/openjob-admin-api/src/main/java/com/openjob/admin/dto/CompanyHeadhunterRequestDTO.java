package com.openjob.admin.dto;

import com.openjob.common.model.User;
import lombok.Getter;

@Getter
public class CompanyHeadhunterRequestDTO {

    private String companyName;
    private User headHunter;

}
