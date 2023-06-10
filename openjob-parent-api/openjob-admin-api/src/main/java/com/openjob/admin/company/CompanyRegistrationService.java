package com.openjob.admin.company;

import com.openjob.common.model.CompanyRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyRegistrationService {
    private final CompanyRegistrationRepository companyRegistrationRepo;

    public Page<CompanyRegistration> findAll(Specification<CompanyRegistration> specification, Pageable pageable){
        return companyRegistrationRepo.findAll(specification, pageable);
    }
}
