package com.openjob.admin.company;

import com.openjob.admin.business.OpenjobBusinessService;
import com.openjob.admin.setting.SettingService;
import com.openjob.admin.util.CustomJavaMailSender;
import com.openjob.common.enums.MailCase;
import com.openjob.common.enums.MemberType;
import com.openjob.common.enums.Role;
import com.openjob.common.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService  {
    private final CompanyRepository companyRepo;
    private final HrService hrService;
    private final CompanyRegistrationService companyRegistrationService;
    private final SettingService settingService;
    private final CustomJavaMailSender mailSender;
    private final OpenjobBusinessService openjobBusinessService;

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
        Company company = companyRepo.findById(id).orElseThrow();
        companyRepo.delete(company);
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

    public void approve(List<CompanyRegistration> companyRegistrationList) {
        OpenjobBusiness openjobBusiness = openjobBusinessService.get();
        companyRegistrationList.forEach(companyRegistration -> {
            Company company = new Company();
            company.setName(companyRegistration.getCompanyName());
            company.setEmail(companyRegistration.getEmail());
            company.setIsActive(true);
            company.setMemberType(MemberType.DEFAULT);
            company.setAmountOfFreeCvViews(openjobBusiness.getFreeCvView());
            company.setAmountOfFreeJobs(openjobBusiness.getFreeJob());

            Company savedCompany = save(company);

            User hr = new User();
            hr.setRole(Role.HR);
            hr.setIsActive(true);
            hr.setFirstName(companyRegistration.getHeadHunterName());
            hr.setEmail(companyRegistration.getEmail());
            hr.setPhone(companyRegistration.getPhone());
            hr.setPosition(companyRegistration.getPosition());
            hr.setCompany(savedCompany);

            User savedHr =hrService.create(hr);
            savedCompany.setHeadHunter(savedHr);
            companyRepo.save(savedCompany);

            companyRegistrationService.deleteById(companyRegistration.getId());

            if (Objects.nonNull(savedHr)){
                MailSetting mailSetting = new MailSetting(
                        savedCompany.getEmail(),
                        "Tài khoản đã được tạo",
                        settingService.getByName(MailCase.MAIL_COMPANY_CREATED.name()).orElseThrow().getValue(),
                        null,
                        savedCompany,
                        null,
                        null);
                mailSender.sendMail(mailSetting); // async
            }
        });
    }

    public void reject(List<CompanyRegistration> companyRegistrationList) {
        List<String> ids = companyRegistrationList.stream().map(CompanyRegistration::getId).collect(Collectors.toList());
        companyRepo.rejectManyCompaniesByIds(ids);
    }

}
