package com.openjob.admin.hr;

import com.openjob.admin.base.AbstractBaseService;
import com.openjob.common.model.HR;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HrService extends AbstractBaseService<HR> implements UserDetailsService {
    private final HrRepository hrRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public Boolean isExisting(String id){
        if (Objects.nonNull(id) && !id.isBlank()){
            return get(id).isPresent();
        }
        return false;
    }


    @Override
    public Optional<HR> get(String id) {
        return hrRepo.findById(id);
    }

    @Override
    public HR save(HR object) throws SQLException {
        return null;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
