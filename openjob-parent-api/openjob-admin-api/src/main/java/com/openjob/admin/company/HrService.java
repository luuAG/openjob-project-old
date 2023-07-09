package com.openjob.admin.company;

import com.openjob.admin.setting.SettingService;
import com.openjob.admin.trackinginvoice.InvoiceService;
import com.openjob.admin.util.AuthenticationUtils;
import com.openjob.admin.util.CustomJavaMailSender;
import com.openjob.common.enums.MailCase;
import com.openjob.common.enums.ServiceType;
import com.openjob.common.model.Invoice;
import com.openjob.common.model.MailSetting;
import com.openjob.common.model.User;
import com.openjob.common.util.CloudinaryUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HrService {
    private final HrRepository hrRepo;
    private final SettingService settingService;
    private final CustomJavaMailSender mailSender;
    private final CompanyRepository companyRepo;
    private final AuthenticationUtils authenticationUtils;
    private final InvoiceService invoiceService;

    public void activate(String companyId){
        hrRepo.activate(companyId);
    }

    public void deactivate(String companyId){
        hrRepo.deactivate(companyId);
    }

    public User getByCompany(String companyId) {
        Optional<User> optionalUser = hrRepo.findByCompany(companyId);
        if (optionalUser.isPresent()){
            User hr = optionalUser.get();
            hr.getCompany().initializeImageUrls();
            return hr;
        }
        else
            throw new IllegalArgumentException("HR not found for company ID: " + companyId);
    }

    public User create(User hr) {
        try {
//            hr.setPassword(passwordEncoder.encode(hr.getPassword()));
            return hrRepo.save(hr);
        } catch (DataIntegrityViolationException e){
            throw new DataIntegrityViolationException(NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        }
    }

    public User update(User hr, HttpServletRequest request) throws IOException {
        try {
            if (hrRepo.findById(hr.getId()).isEmpty())
                return null;

            if (hr.getCompany() != null){
                // tracking updating account balance
                double oldBalance = companyRepo.getAccountBalance(hr.getCompany().getId());
                if (hr.getCompany().getAccountBalance() != oldBalance){
                    Invoice invoice = new Invoice();
                    invoice.setCompanyId(hr.getCompany().getId());
                    invoice.setCompanyName(hr.getCompany().getName());
                    invoice.setServiceType(ServiceType.ADMIN_UPDATE);
                    invoice.setAmount(hr.getCompany().getAccountBalance() - oldBalance);
                    invoice.setCreatedAt(new Date());
                    invoice.setCreatedBy(authenticationUtils.getLoggedInUser(request).getFirstName());
                    invoiceService.save(invoice);
                }

                if (Objects.nonNull(hr.getCompany().getLogoUrl()) && hr.getCompany().getLogoUrl().startsWith("data:")){
                    String base64Image = hr.getCompany().getLogoUrl().split(",")[1];
                    byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);

                    CloudinaryUtils.getInstance();
                    String returnedUrl = CloudinaryUtils.upload(imageBytes, "companyLogo/"+hr.getCompany().getId());
                    hr.getCompany().setLogoUrl(returnedUrl);
                }
                if (Objects.nonNull(hr.getCompany().getBase64Images()) && hr.getCompany().getBase64Images().length > 0) {
                    List<String> urls;
                    if (hr.getCompany().getImageUrlsString() != null){
                        urls= Arrays.stream(hr.getCompany().getImageUrlsString().split(", ")).collect(Collectors.toList());
                        urls.remove(null);
                        urls.remove("");
                    } else {
                        urls = new ArrayList<>();
                    }
                    for (String rawBase64Image : hr.getCompany().getBase64Images()){
                        if (rawBase64Image != null){
                            String base64Image = rawBase64Image.split(",")[1];
                            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);

                            CloudinaryUtils.getInstance();
                            String returnedUrl = CloudinaryUtils.upload(imageBytes, "companyImages/"+ UUID.randomUUID());
                            urls.add(returnedUrl);
                        }
                    }
                    hr.getCompany().setImageUrlsStringCustom(urls);
                }
            }
            // mail to company if deactivate
            if (companyRepo.checkActiveById(hr.getCompany().getId()) && !hr.getCompany().getIsActive()){
                MailSetting mailSetting = new MailSetting(
                        hr.getCompany().getEmail(),
                        "Tài khoản của công ty đã bị vô hiệu hoá",
                        settingService.getByName(MailCase.MAIL_COMPANY_DEACTIVATED.name()).orElseThrow().getValue(),
                        null,
                        hr.getCompany(),
                        null,
                        null);
                mailSender.sendMail(mailSetting); // async
            }
            // mail to company if activate
            if (!companyRepo.checkActiveById(hr.getCompany().getId()) && hr.getCompany().getIsActive()){
                MailSetting mailSetting = new MailSetting(
                        hr.getCompany().getEmail(),
                        "Tài khoản của công ty đã được kích hoạt",
                        settingService.getByName(MailCase.MAIL_COMPANY_REACTIVATED.name()).orElseThrow().getValue(),
                        null,
                        hr.getCompany(),
                        null,
                        null);
                mailSender.sendMail(mailSetting); // async
            }
            return hrRepo.save(hr);
        } catch (DataIntegrityViolationException e){
            throw new DataIntegrityViolationException(NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        }
    }

    public void delete(User hr) {
        hrRepo.delete(hr);
    }
}
