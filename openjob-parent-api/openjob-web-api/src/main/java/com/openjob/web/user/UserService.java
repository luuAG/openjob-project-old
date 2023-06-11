package com.openjob.web.user;


import com.openjob.common.model.User;
import com.openjob.common.util.CloudinaryUtils;
import com.openjob.web.dto.UserCvDto;
import com.openjob.web.util.NullAwareBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public Optional<User> get(String id) {
        return userRepo.findById(id);
    }


    public User save(User user, boolean withPassword) {
        if (withPassword){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepo.save(user);
    }

    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    public User getByEmail(String email) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        return userOptional.orElse(null);
    }

    public boolean existById(String id) {
        return userRepo.existsById(id);
    }

    public User patchUpdate(User userInfo) throws InvocationTargetException, IllegalAccessException, IOException {
        User existingUser = userRepo.getById(userInfo.getId());
        NullAwareBeanUtils.getInstance().copyProperties(existingUser, userInfo);
        if (Objects.nonNull(userInfo.getCompany()) && Objects.nonNull(userInfo.getCompany().getLogoUrl()) && userInfo.getCompany().getLogoUrl().startsWith("data:")){
            String base64Image = userInfo.getCompany().getLogoUrl().split(",")[1];
            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);

            CloudinaryUtils.getInstance();
            String returnedUrl = CloudinaryUtils.upload(imageBytes, userInfo.getCompany().getId());
            existingUser.getCompany().setLogoUrl(returnedUrl);
        }
        if (Objects.nonNull(userInfo.getCompany()) && userInfo.getCompany().getDescription().contains("<img")){
            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(
                                                                    getImgBase64FromText(
                                                                            userInfo.getCompany().getDescription()));

            CloudinaryUtils.getInstance();
            String returnedUrl = CloudinaryUtils.upload(imageBytes, UUID.randomUUID().toString());
            userInfo.getCompany().setDescription(replaceImgTag(userInfo.getCompany().getDescription(), returnedUrl));
        }
        if (Objects.nonNull(userInfo.getCompany()) && Objects.nonNull(userInfo.getCompany().getBase64Images())) {
            List<String> urls = Arrays.stream(userInfo.getCompany().getImageUrlsString().split(", ")).collect(Collectors.toList());
            urls.remove(null);
            urls.remove("");
            for (String rawBase64Image : userInfo.getCompany().getBase64Images()){
                String base64Image = rawBase64Image.split(",")[1];
                byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);

                CloudinaryUtils.getInstance();
                String returnedUrl = CloudinaryUtils.upload(imageBytes, "companyImages/"+ UUID.randomUUID());
                urls.add(returnedUrl);
            }
            existingUser.getCompany().setImageUrlsStringCustom(urls);
        }
        return userRepo.save(existingUser);
    }

    private String getImgBase64FromText(String text){
        int startIndex = text.indexOf("base64,") + 7;
        StringBuilder base64 = new StringBuilder();
        for (int i=startIndex; i<text.length(); i++){
            if (text.charAt(i) == '"')
                break;
            base64.append(text.charAt(i));
        }
        return base64.toString();
    }
    private String replaceImgTag(String text, String imageUrl) {
        int imgTagIndex = text.indexOf("<img");
        while (text.charAt(imgTagIndex) != '>'){
            text = text.replace(String.valueOf(text.charAt(imgTagIndex)), "");
        }
        text = text.replace(String.valueOf(text.charAt(imgTagIndex)), "");
        return insertString(text, "<img src='"+imageUrl+"'/>", imgTagIndex);
    }
    private String insertString(
            String originalString,
            String stringToBeInserted,
            int index)
    {
        StringBuilder newString = new StringBuilder();

        for (int i = 0; i < originalString.length(); i++) {
            newString.append(originalString.charAt(i));

            if (i == index) {
                newString.append(stringToBeInserted);
            }
        }
        return newString.toString();
    }

    public List<UserCvDto> getByMatchingJob(String jobId) {
        return userRepo.findByMatchingJob(jobId);
    }

    public List<UserCvDto> getByJobApplied(String jobId) {
        return userRepo.findAppliedJob(jobId);
    }
}
