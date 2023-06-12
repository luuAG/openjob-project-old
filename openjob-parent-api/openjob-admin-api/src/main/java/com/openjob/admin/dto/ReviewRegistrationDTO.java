package com.openjob.admin.dto;

import com.openjob.common.model.CompanyRegistration;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReviewRegistrationDTO {
    private List<CompanyRegistration> companyRegistrationList;
    private boolean isApproved;
}
