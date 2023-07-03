package com.openjob.admin.company;

import com.openjob.admin.dto.*;
import com.openjob.admin.setting.SettingService;
import com.openjob.admin.util.CustomJavaMailSender;
import com.openjob.common.enums.AuthProvider;
import com.openjob.common.enums.MailCase;
import com.openjob.common.enums.MemberType;
import com.openjob.common.enums.Role;
import com.openjob.common.model.*;
import com.openjob.common.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;


@RestController
@RequiredArgsConstructor
public class CompanyController {
    private final HrService hrService;
    private final CompanyService companyService;
    private final SettingService settingService;
    private final CustomJavaMailSender mailSender;
    private final CompanyRegistrationService companyRegistrationService;

    @GetMapping(path = "/company/{id}/hr", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getHr(@PathVariable("id") String id) {
        if (Objects.isNull(id)){
            throw new IllegalArgumentException("ID is null");
        }
        User hr = hrService.getByCompany(id);
        return ResponseEntity.ok(hr);
    }

    @GetMapping(path = "/company/check_exist/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> checkCompanyExistByName(@PathVariable("name") String name) {
        if (Objects.isNull(name) || name.isBlank()){
            throw new IllegalArgumentException("Name is null or blank");
        }
        boolean isExisting = companyService.isExistByName(name);
        if (isExisting)
            return ResponseEntity.badRequest().body(new MessageResponse("Company name exist"));
        return ResponseEntity.ok(new MessageResponse("Accepted"));
    }


    @PostMapping(path = "/company/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyHeadhunterResponseDTO> createHeadHunter(@RequestBody CompanyCreateRequestDTO body) throws SQLException {
        User hr = body.getHeadHunter();
        Company company = body.getHeadHunter().getCompany();

        if (Objects.isNull(hr)){
            throw new IllegalArgumentException("Head hunter is null");
        }
        if (Objects.isNull(hr.getEmail()) || hr.getEmail().isBlank()){
            throw new IllegalArgumentException("Head hunter's email is null or blank");
        }
        if (Objects.isNull(company.getName()) || company.getName().isBlank()){
            throw new IllegalArgumentException("Company name is null or blank");
        }
        company.setIsActive(true);
        company.setMemberType(MemberType.DEFAULT);
        Company savedCompany = companyService.save(company);

        hr.setCompany(savedCompany);
        hr.setRole(Role.HR);
        hr.setAuthProvider(AuthProvider.DATABASE);
        User savedHr = hrService.create(hr);
        savedCompany.setHeadHunter(savedHr);
        companyService.save(savedCompany);

        if (Objects.nonNull(savedHr)){
            MailSetting mailSetting = new MailSetting(
                    company.getEmail(),
                    "Tài khoản đã được tạo",
                    settingService.getByName(MailCase.MAIL_COMPANY_CREATED.name()).orElseThrow().getValue(),
                    null,
                    company,
                    null,
                    null);
            mailSender.sendMail(mailSetting); // async
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CompanyHeadhunterResponseDTO(savedHr.getCompany().getId(), savedHr.getId())
        );
    }

    @GetMapping(path = "/companies", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyPaginationDTO> getCompanies(
            @And({
                    @Spec(path = "createdAt", params = {"startDate", "endDate"}, spec = Between.class),
                    @Spec(path = "memberType", spec = Equal.class),
                    @Spec(path = "address", spec = Like.class),
                    @Spec(path = "companyType", spec = Equal.class),
                    @Spec(path = "isActive", spec = Equal.class),
                    @Spec(path = "name", spec = Like.class),
            }) Specification<Company> companySpec,
            PagingModel pagingModel) {
        Page<Company> pageCompany = companyService.search(companySpec, pagingModel.getPageable());
        return ResponseEntity.ok(new CompanyPaginationDTO(
                pageCompany.getContent(),
                pageCompany.getTotalPages(),
                pageCompany.getTotalElements())
        );
    }

    @DeleteMapping(path = "/company/{companyId}/delete")
    public ResponseEntity<MessageResponse> deleteCompanyAndHR(
            @PathVariable("companyId") String companyId) {
        User hr = hrService.getByCompany(companyId);
        hrService.delete(hr);
        return ResponseEntity.ok(
                new MessageResponse("Company: " + hr.getCompany().getName()
                        + " with HR account: " + hr.getEmail() + " is deleted")
        );
    }


    @PostMapping(path = "/company/{companyId}/hr/update")
    public ResponseEntity<User> updateHrAccountInfo(
            @PathVariable("companyId") String companyId,
            @RequestBody User hr) throws IOException {
        if (!companyService.existsById(companyId))
            throw new IllegalArgumentException("Company not found!");
        User updatedUser = hrService.update(hr);
        if (Objects.nonNull(updatedUser))
            return ResponseEntity.ok(updatedUser);
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping(path = "/company/unapproved")
    public ResponseEntity<CompanyRegistrationPaginationDTO> getUnapprovedCompanies(
            @And({
                    @Spec(path = "createdAt", params = {"startDate", "endDate"}, spec = Between.class),
                    @Spec(path = "companyName", spec = Like.class),
            }) Specification<CompanyRegistration> companySpec,
            PagingModel pagingModel) {
        Page<CompanyRegistration> pageCompany = companyRegistrationService.search(companySpec, pagingModel.getPageable());
        return ResponseEntity.ok(new CompanyRegistrationPaginationDTO(
                pageCompany.getContent(),
                pageCompany.getTotalPages(),
                pageCompany.getTotalElements())
        );
    }

    @PostMapping(path = "/company/review-registration")
    public ResponseEntity<MessageResponse> reviewManyCompanies(@RequestBody ReviewRegistrationDTO dto){
        if (dto.isApproved())
            companyService.approve(dto.getCompanyRegistrationList());
        else
            companyService.reject(dto.getCompanyRegistrationList());
        return ResponseEntity.ok(new MessageResponse("Duyệt công ty thành công!"));
    }

}
