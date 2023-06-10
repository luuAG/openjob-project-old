package com.openjob.admin.company;

import com.openjob.common.model.User;
import com.openjob.common.util.CloudinaryUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HrService {
    private final HrRepository hrRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public void activate(String companyId){
        hrRepo.activate(companyId);
    }

    public void deactivate(String companyId){
        hrRepo.deactivate(companyId);
    }

    public User getByCompany(String companyId) {
        Optional<User> optionalUser = hrRepo.findByCompany(companyId);
        if (optionalUser.isPresent())
            return optionalUser.get();
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

    public User update(User hr) throws IOException {
        try {
            if (hrRepo.findById(hr.getId()).isEmpty())
                return null;

            if (hr.getCompany() != null){
                if (Objects.nonNull(hr.getCompany().getLogoUrl()) && hr.getCompany().getLogoUrl().startsWith("data:")){
                    String base64Image = hr.getCompany().getLogoUrl().split(",")[1];
                    byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);

                    CloudinaryUtils.getInstance();
                    String returnedUrl = CloudinaryUtils.upload(imageBytes, "companyLogo/"+hr.getCompany().getId());
                    hr.getCompany().setLogoUrl(returnedUrl);
                }
                if (Objects.nonNull(hr.getCompany().getBase64Images())) {
                    List<String> urls = new ArrayList<>();
                    for (String rawBase64Image : hr.getCompany().getBase64Images()){
                        String base64Image = rawBase64Image.split(",")[1];
                        byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);

                        CloudinaryUtils.getInstance();
                        String returnedUrl = CloudinaryUtils.upload(imageBytes, "companyImages/"+ UUID.randomUUID());
                        urls.add(returnedUrl);
                    }
                    hr.getCompany().setImageUrlsStringCustom(urls);
                }
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
