package com.openjob.admin.webuser;

import com.openjob.admin.base.AbstractBaseService;
import com.openjob.common.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WebUserService extends AbstractBaseService<User> {
    private final WebUserRepository webUserRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public Boolean isExisting(String id){
        if (Objects.nonNull(id) && !id.isBlank()){
            return get(id).isPresent();
        }
        return false;
    }


    @Override
    public Optional<User> get(String id) {
        return webUserRepo.findById(id);
    }

    @Override
    public User save(User object) throws SQLException {
        object.setPassword(passwordEncoder.encode(object.getPassword()));
        return webUserRepo.save(object);
    }

    @Override
    public User saveWithoutPassword(User object) throws SQLException {
        return webUserRepo.save(object);
    }

    @Override
    public void delete(String id) {

    }

    public Page<User> searchByCompany(Integer page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(keyword) || keyword.isBlank())
            keyword = "";
        Page<User> pageUser = webUserRepo.searchByCompany(keyword ,pageable);
        return pageUser;
    }

    public Page<User> searchByKeyword(Integer page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(keyword) || keyword.isBlank())
            keyword = "";
        Page<User> pageUser = webUserRepo.searchByKeyword(keyword ,pageable);
        return pageUser;
    }
}
