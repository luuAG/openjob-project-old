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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        if (Objects.nonNull(userInfo.getCompany().getLogoUrl()) && userInfo.getCompany().getLogoUrl().startsWith("data:")){
            String base64Image = userInfo.getCompany().getLogoUrl().split(",")[1];
            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);

            CloudinaryUtils.getInstance();
            String returnedUrl = CloudinaryUtils.upload(imageBytes, userInfo.getCompany().getId());
            existingUser.getCompany().setLogoUrl(returnedUrl);
        }
        return userRepo.save(existingUser);
    }

    public List<UserCvDto> getByMatchingJob(String jobId) {
        return userRepo.findByMatchingJob(jobId);
    }
}
