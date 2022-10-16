package com.openjob.admin.webuser;

import com.openjob.common.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WebUserService {
    private final WebUserRepository webUserRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public Boolean isExisting(String id){
        if (Objects.nonNull(id) && !id.isBlank()){
            return get(id).isPresent();
        }
        return false;
    }

    public Optional<User> get(String id) {
        return webUserRepo.findById(id);
    }

    public void activate(String id) {
        User user = webUserRepo.getById(id);
        user.setIsActive(true);
        webUserRepo.save(user);
    }
    public void deactivate(String id) {
        User user = webUserRepo.getById(id);
        user.setIsActive(false);
        webUserRepo.save(user);
    }

    public Page<User> searchByKeyword(Integer page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(keyword) || keyword.isBlank())
            keyword = "";
        Page<User> pageUser = webUserRepo.searchByKeyword(keyword ,pageable);
        return pageUser;
    }
}
