package com.openjob.web.company;

import com.openjob.common.model.CompanyRegistration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class CompanyRegistrationService {
    private final CompanyRegistrationRepository companyRegistrationRepo;

    public Page<CompanyRegistration> search(Specification<CompanyRegistration> companySpec, Pageable pageable) {
        return companyRegistrationRepo.findAll(companySpec, pageable);
    }

    public void deleteById(String id) {
        companyRegistrationRepo.deleteById(id);
    }

    public void save(CompanyRegistration registration){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        registration.setCreatedBy(username);
        registration.setCreatedAt(new Date());
        companyRegistrationRepo.save(registration);
    }
}
