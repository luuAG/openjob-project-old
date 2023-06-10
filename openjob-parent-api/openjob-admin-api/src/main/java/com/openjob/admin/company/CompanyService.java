package com.openjob.admin.company;

import com.openjob.common.model.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService  {
    private final CompanyRepository companyRepo;

    public Optional<Company> get(String id)  {
        return companyRepo.findById(id);
    }

    public Company save(Company object)  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        object.setUpdatedAt(new Date());
        object.setUpdatedBy(username);
        if (object.getId() == null) {
            object.setCreatedAt(new Date());
            object.setCreatedBy(username);
        }
        return companyRepo.save(object);
    }

    public void delete(String id)  {
        Company company = companyRepo.findById(id).get();
        companyRepo.delete(company);
    }

    public Page<Company> search(Integer page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(keyword) || keyword.isBlank()) {
            return companyRepo.findAll(pageable);
        }
        return companyRepo.findByKeyword(keyword, pageable);
    }

    public boolean isExistByName(String name) {
        return companyRepo.findByName(name).isPresent();
    }

    public boolean existsById(String companyId) {
        return companyRepo.existsById(companyId);
    }

    public Page<Company> search(Specification<Company> companySpec, Pageable pageable) {
        return companyRepo.findAll(companySpec, pageable);
    }
}
