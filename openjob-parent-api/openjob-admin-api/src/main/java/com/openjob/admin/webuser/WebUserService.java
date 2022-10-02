package com.openjob.admin.webuser;

import com.openjob.admin.base.AbstractBaseService;
import com.openjob.common.model.WebUser;
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
public class WebUserService extends AbstractBaseService<WebUser> {
    private final WebUserRepository webUserRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public Boolean isExisting(String id){
        if (Objects.nonNull(id) && !id.isBlank()){
            return get(id).isPresent();
        }
        return false;
    }


    @Override
    public Optional<WebUser> get(String id) {
        return webUserRepo.findById(id);
    }

    @Override
    public WebUser save(WebUser object) throws SQLException {
        object.setPassword(passwordEncoder.encode(object.getPassword()));
        return webUserRepo.save(object);
    }

    @Override
    public WebUser saveWithoutPassword(WebUser object) throws SQLException {
        return webUserRepo.save(object);
    }

    @Override
    public void delete(String id) {

    }

    public Page<WebUser> searchByCompany(Integer page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(keyword) || keyword.isBlank())
            keyword = "";
        Page<WebUser> pageUser = webUserRepo.searchByCompany(keyword ,pageable);
        return pageUser;
    }

    public Page<WebUser> searchByKeyword(Integer page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(keyword) || keyword.isBlank())
            keyword = "";
        Page<WebUser> pageUser = webUserRepo.searchByKeyword(keyword ,pageable);
        return pageUser;
    }
}
