package com.openjob.admin.webuser;

import com.openjob.admin.base.AbstractBaseService;
import com.openjob.common.model.WebUser;
import lombok.RequiredArgsConstructor;
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
        return Optional.empty();
    }

    @Override
    public WebUser save(WebUser object) throws SQLException {
        return null;
    }

    @Override
    public void delete(String id) {

    }

}
