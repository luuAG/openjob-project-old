package com.openjob.web.company;

import com.openjob.common.enums.MemberType;
import com.openjob.common.model.Company;
import com.openjob.web.business.OpenjobBusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {
    private final CompanyRepository companyRepo;
    private final OpenjobBusinessService openjobBusinessService;


    public Company getById(String id){
        return companyRepo.findById(id).orElse(null);
    }


    public Page<Company> searchCompany(Integer page, Integer size, String keyword, String location) {
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(keyword) || keyword.isBlank()){
            if (Objects.isNull(location) || location.isBlank()){
                return companyRepo.findAll(pageable);
            } else {
                return companyRepo.findByLocation(location, pageable);
            }
        } else {
            if (Objects.isNull(location) || location.isBlank()){
                return companyRepo.findByKeyword(keyword, pageable);
            } else {
                return companyRepo.findByKeywordAndLocation(keyword, location, pageable);
            }
        }
    }

    public void updateAccountBalance(String companyId, Double amount) {
        companyRepo.updateAccountBalance(companyId, amount);
    }

    public void upgradeMembership(String companyId) {
        Company company = getById(companyId);
        company.setMemberType(MemberType.PREMIUM);
        company.setUpdatedAt(new Date());
        company.setAccountBalance(company.getAccountBalance() - openjobBusinessService.get().getPremiumPrice());
        companyRepo.save(company);
    }
}
