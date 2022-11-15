package com.openjob.admin.company;

import com.openjob.admin.dto.CompanyCreateRequestDTO;
import com.openjob.admin.dto.CompanyHeadhunterResponseDTO;
import com.openjob.admin.dto.CompanyPaginationDTO;
import com.openjob.common.enums.AuthProvider;
import com.openjob.common.enums.Role;
import com.openjob.common.model.Company;
import com.openjob.common.model.User;
import com.openjob.common.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Objects;


@RestController
@RequiredArgsConstructor
public class CompanyController {
    private final HrService hrService;
    private final CompanyService companyService;
    private final JavaMailSender mailSender;

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
        Company company = new Company();
        company.setName(body.getCompanyName());

        if (Objects.isNull(hr)){
            throw new IllegalArgumentException("Head hunter is null");
        }
        if (Objects.isNull(hr.getEmail()) || hr.getEmail().isBlank()){
            throw new IllegalArgumentException("Head hunter's email is null or blank");
        }
        if (Objects.isNull(company.getName()) || company.getName().isBlank()){
            throw new IllegalArgumentException("Company name is null or blank");
        }

        company.setHeadHunter(hr);

        hr.setCompany(company);
        hr.setRole(Role.HR);
        hr.setPassword("12345678");
        hr.setAuthProvider(AuthProvider.DATABASE);
        User savedHr = hrService.create(hr);

        if (Objects.nonNull(savedHr)){
            MimeMessagePreparator message = mimeMessage -> {
                MimeMessageHelper message1 = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message1.setFrom("duongvannam2001@gmail.com");
                message1.setTo(savedHr.getEmail());
                message1.setSubject("Tài khoản OpenJob đã được tạo");
                message1.setText("Kính gửi Phòng tuyển dụng công ty "+company.getName()+", \n" +
                        "Tài khoản cho Quản lý bộ phận tuyển dụng ở OpenJob: \n" +
                        "Username: "+ savedHr.getEmail() + "\n" +
                        "Password: 12345678\n" +
                        "Vui lòng đổi mật khẩu sau khi đăng nhập lần đầu!", true);
            };
            try {
                mailSender.send(message);
            } catch (Exception ex) {
                hrService.delete(savedHr);
                throw ex;
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new CompanyHeadhunterResponseDTO(savedHr.getCompany().getId(), savedHr.getId())
        );
    }

    @GetMapping(path = "/companies", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyPaginationDTO> getCompanies(
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestParam(required = false) String keyword) {
        Page<Company> pageCompany = companyService.search(page, size, keyword);
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
            @RequestParam("updatePassword") Boolean updatePassword,
            @RequestBody User hr) {
        if (!companyService.existsById(companyId))
            throw new IllegalArgumentException("Company not found!");
        User updatedUser = hrService.update(hr, updatePassword);
        if (Objects.nonNull(updatedUser))
            return ResponseEntity.ok(updatedUser);
        return ResponseEntity.badRequest().body(null);
    }
}
