package com.openjob.web.company;

import com.openjob.common.model.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepo;

    public Company getById(String id){
        return companyRepo.findById(id).orElse(null);
    }
}
