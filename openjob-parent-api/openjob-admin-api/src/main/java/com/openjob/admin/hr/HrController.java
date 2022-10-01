package com.openjob.admin.hr;

import com.openjob.admin.dto.HrPaginationDTO;
import com.openjob.admin.exception.UserNotFoundException;
import com.openjob.common.model.Company;
import com.openjob.common.model.HR;
import com.openjob.common.model.Role;
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

import javax.mail.internet.MimeMessage;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
public class HrController {
    private final HrService hrService;
    private final JavaMailSender mailSender;

    @GetMapping(path = "/hr/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getHr(@PathVariable("id") String id) {
        if (Objects.isNull(id)){
            throw new IllegalArgumentException("ID is null");
        }
        Optional<HR> hrOptional = hrService.get(id);
        if (hrOptional.isPresent())
            return ResponseEntity.ok(hrOptional.get());
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping(path = "/hrs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HrPaginationDTO> getAdminUserByPage(
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean byCompany){
        Page<HR> pageHr;
        if (Objects.nonNull(byCompany) && byCompany)
            pageHr = hrService.searchByCompany(page, size, keyword);
        else
            pageHr = hrService.searchByKeyword(page, size, keyword);
        return ResponseEntity.ok(new HrPaginationDTO(
                pageHr.getContent(),
                pageHr.getTotalPages(),
                pageHr.getTotalElements()
        ));
    }

    @PostMapping(path = "/hr/activate/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> activateHr(@PathVariable String id) throws SQLException, UserNotFoundException {
            Optional<HR> optionalHR = hrService.get(id);
            if (optionalHR.isPresent()){
                HR existingHr = optionalHR.get();
                existingHr.setIsActive(true);
                hrService.save(existingHr);
            } else {
                throw new UserNotFoundException("HR user not found with ID: " + id);
            }
            return ResponseEntity.ok(new MessageResponse("HR user is activated, ID: " + id));
    }

    @PostMapping(path = "/hr/deactivate/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> deactivateHr(@PathVariable String id) throws SQLException, UserNotFoundException {
        Optional<HR> optionalHR = hrService.get(id);
        if (optionalHR.isPresent()){
            HR existingHr = optionalHR.get();
            existingHr.setIsActive(false);
            hrService.save(existingHr);
        } else {
            throw new UserNotFoundException("HR user not found with ID: " + id);
        }
        return ResponseEntity.ok(new MessageResponse("HR user is deactivated, ID: " + id));
    }

    @PostMapping(path = "/hr/create-head-hunter", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HR> createHeadHunter(@RequestBody Map<String, Object> body) throws SQLException {
        HR hr = (HR) body.get("headHunter");
        Company company = (Company) body.get("company");

        if (Objects.isNull(hr) || Objects.isNull(company)){
            throw new IllegalArgumentException("Object is null");
        }
        if (Objects.isNull(hr.getEmail()) || hr.getEmail().isBlank()){
            throw new IllegalArgumentException("Head hunter's email is null or blank");
        }
        if (Objects.isNull(company.getName()) || company.getName().isBlank()){
            throw new IllegalArgumentException("Company name is null or blank");
        }

        company.setHeadHunter(hr);
        hr.setCompany(company);
        hr.setRole(Role.HEAD_HUNTER);
        hr.setPassword("12345678");
        HR savedHr = hrService.save(hr);
        if (Objects.nonNull(savedHr)){
            MimeMessagePreparator message = new MimeMessagePreparator() {
                @Override
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                    message.setFrom("duongvannam2001@gmail.com");
                    message.setTo(savedHr.getEmail());
                    message.setSubject("Tài khoản OpenJob đã được tạo");
                    message.setText("Kính gửi Phòng tuyển dụng công ty "+company.getName()+", \n" +
                            "Tài khoản cho Quản lý bộ phận tuyển dụng ở OpenJob: \n" +
                            "Username: "+ savedHr.getEmail() + "\n" +
                            "Password: 12345678\n" +
                            "Vui lòng đổi mật khẩu sau khi đăng nhập lần đầu!", true);
                }
            };
            mailSender.send(message);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHr);
    }



}
