package com.openjob.admin.company;

import com.openjob.common.model.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
}
