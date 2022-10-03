package com.openjob.admin.company;

import com.openjob.admin.base.AbstractBaseService;
import com.openjob.admin.exception.UserNotFoundException;
import com.openjob.common.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class HrService extends AbstractBaseService<User>  {
    private final HrRepository hrRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public Boolean isExisting(String id){
        if (Objects.nonNull(id) && !id.isBlank()){
            return get(id).isPresent();
        }
        return false;
    }


    @Override
    public Optional<User> get(String id) {
        return hrRepo.findById(id);
    }

    @Override
    public User save(User object) throws SQLException {
        object.setPassword(passwordEncoder.encode(object.getPassword()));
        try {
            return hrRepo.save(object);
        } catch (Exception ex){
            throw new SQLException(NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
        }
    }

    @Override
    public User saveWithoutPassword(User object) throws SQLException {
        try {
            return hrRepo.save(object);
        } catch (Exception ex){
            throw new SQLException(NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
        }
    }

    @Override
    public void delete(String id) throws UserNotFoundException {
        Optional<User> hr = hrRepo.findById(id);
        if (hr.isPresent())
            hrRepo.delete(hr.get());
        else
            throw new UserNotFoundException("User not found for ID: " + id);
    }



    public Page<User> searchByKeyword(Integer page, Integer size, String keyword) {
        if (Objects.isNull(keyword) || keyword.isEmpty())
            return getAll(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<User> pageHr = hrRepo.search(keyword, pageable);
        return pageHr;
    }

    private Page<User> getAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> pageHr = hrRepo.findAll(pageable);
        return pageHr;
    }

    public Page<User> searchByCompany(Integer page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(keyword) || keyword.isBlank())
            keyword = "";
        Page<User> pageHr = hrRepo.searchByCompany(keyword ,pageable);
        return pageHr;
    }

    public Page<User> findByCompanyId(Integer page, Integer size, String companyId) {
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(companyId) || companyId.isBlank())
            throw new IllegalArgumentException("Company ID is null or blank");
        Page<User> pageHr = hrRepo.findByCompanyId(companyId, pageable);
        return pageHr;
    }
}
