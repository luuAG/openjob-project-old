package com.openjob.admin.company;

import com.openjob.admin.dto.*;
import com.openjob.admin.setting.SettingService;
import com.openjob.admin.util.CustomJavaMailSender;
import com.openjob.common.enums.AuthProvider;
import com.openjob.common.enums.Role;
import com.openjob.common.model.Company;
import com.openjob.common.model.CompanyRegistration;
import com.openjob.common.model.PagingModel;
import com.openjob.common.model.User;
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
        Company savedCompany = companyService.save(company);

        hr.setCompany(savedCompany);
        hr.setRole(Role.HR);
        hr.setAuthProvider(AuthProvider.DATABASE);
        User savedHr = hrService.create(hr);

//        if (Objects.nonNull(savedHr)){
//            MimeMessagePreparator message = mimeMessage -> {
//                MimeMessageHelper message1 = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//                message1.setFrom("duongvannam2001@gmail.com");
//                message1.setTo(savedHr.getEmail());
//                message1.setSubject("Tài khoản OpenJob đã được tạo");
//                String text = settingService.getByName("MAIL_NEW_HR_ACCOUNT").orElseThrow().getValue();
//                text = text.replace("[[company]]", company.getName())
//                        .replace("[[email]]", savedHr.getEmail())
//                        .replace("[[password]]", "12345678");
//                message1.setText(text, true);
//            };
//            try {
//                mailSender.reloadProperties();
//                mailSender.getMailSender().send(message);
//            } catch (Exception ex) {
//                hrService.delete(savedHr);
//                throw ex;
//            }
//        }
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CompanyHeadhunterResponseDTO(savedHr.getCompany().getId(), savedHr.getId())
        );
    }

//    @GetMapping(path = "/companies", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<CompanyPaginationDTO> getCompanies(
//            @RequestParam Integer page,
//            @RequestParam Integer size,
//            @RequestParam(required = false) String keyword) {
//        Page<Company> pageCompany = companyService.search(page, size, keyword);
//        return ResponseEntity.ok(new CompanyPaginationDTO(
//                pageCompany.getContent(),
//                pageCompany.getTotalPages(),
//                pageCompany.getTotalElements())
//        );
//    }

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
