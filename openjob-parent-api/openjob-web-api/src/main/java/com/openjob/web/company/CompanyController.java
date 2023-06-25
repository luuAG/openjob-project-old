package com.openjob.web.company;

import com.openjob.common.model.Company;
import com.openjob.common.model.CompanyRegistration;
import com.openjob.common.response.MessageResponse;
import com.openjob.web.dto.CompanyPaginationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;
    private final CompanyRegistrationService companyRegistrationService;

    @GetMapping(path = "/companies", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CompanyPaginationDTO> getAndSearchCompany(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "location", required = false) String location) {
        Page<Company> companyPage = companyService.searchCompany(page, size, keyword, location);
        return ResponseEntity.ok(new CompanyPaginationDTO(
                companyPage.getContent(),
                companyPage.getTotalPages(),
                companyPage.getTotalElements()
        ));
    }

    @GetMapping(path = "/company/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Company> getCompanyDetails(@PathVariable("id") String id) {
        Company company = companyService.getById(id);
        return Objects.nonNull(company) ?
                ResponseEntity.ok(company) :
                ResponseEntity.notFound().build();
    }

    @PostMapping(path = "/company/register")
    public ResponseEntity<MessageResponse> companyRegister(@RequestBody CompanyRegistration registration){
        companyRegistrationService.save(registration);
        return ResponseEntity.ok(new MessageResponse("Đăng ký thành công! Chúng tôi sẽ liên hệ bạn sớm!"));
    }

    @PostMapping("/company/{companyId}/upgrade")
    public ResponseEntity<MessageResponse> upgradeMembership(@PathVariable("companyId") String companyId){
        companyService.upgradeMembership(companyId);
        return ResponseEntity.ok(new MessageResponse("Thanh toán thành công"));
    }
}
