package com.openjob.admin.company;

import com.openjob.common.model.CompanyRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyRegistrationService {
    private final CompanyRegistrationRepository companyRegistrationRepo;

    public Page<CompanyRegistration> search(Specification<CompanyRegistration> companySpec, Pageable pageable) {
        return companyRegistrationRepo.findAll(companySpec, pageable);
    }

    public void deleteById(String id) {
        companyRegistrationRepo.deleteById(id);
    }
}
