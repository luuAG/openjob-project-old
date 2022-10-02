package com.openjob.admin.company;

import com.openjob.admin.base.AbstractBaseService;
import com.openjob.admin.exception.UserNotFoundException;
import com.openjob.common.model.HR;
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
public class HrService extends AbstractBaseService<HR>  {
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
        object.setPassword(passwordEncoder.encode(object.getPassword()));
        try {
            return hrRepo.save(object);
        } catch (Exception ex){
            throw new SQLException(NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
        }
    }

    @Override
    public HR saveWithoutPassword(HR object) throws SQLException {
        try {
            return hrRepo.save(object);
        } catch (Exception ex){
            throw new SQLException(NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
        }
    }

    @Override
    public void delete(String id) throws UserNotFoundException {
        Optional<HR> hr = hrRepo.findById(id);
        if (hr.isPresent())
            hrRepo.delete(hr.get());
        else
            throw new UserNotFoundException("User not found for ID: " + id);
    }



    public Page<HR> searchByKeyword(Integer page, Integer size, String keyword) {
        if (Objects.isNull(keyword) || keyword.isEmpty())
            return getAll(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<HR> pageHr = hrRepo.search(keyword, pageable);
        return pageHr;
    }

    private Page<HR> getAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HR> pageHr = hrRepo.findAll(pageable);
        return pageHr;
    }

    public Page<HR> searchByCompany(Integer page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(keyword) || keyword.isBlank())
            keyword = "";
        Page<HR> pageHr = hrRepo.searchByCompany(keyword ,pageable);
        return pageHr;
    }

    public Page<HR> findByCompanyId(Integer page, Integer size, String companyId) {
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(companyId) || companyId.isBlank())
            throw new IllegalArgumentException("Company ID is null or blank");
        Page<HR> pageHr = hrRepo.findByCompanyId(companyId, pageable);
        return pageHr;
    }
}
