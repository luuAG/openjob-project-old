package com.openjob.admin.company;

import com.openjob.admin.base.AbstractBaseService;
import com.openjob.admin.exception.UserNotFoundException;
import com.openjob.common.model.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService extends AbstractBaseService<Company>  {
    private final CompanyRepository companyRepo;

    @Override
    public Optional<Company> get(String id) throws IllegalArgumentException {
        return companyRepo.findById(id);
    }

    @Override
    public Company save(Company object) throws SQLException {
        return companyRepo.save(object);
    }

    @Override
    public Company saveWithoutPassword(Company object) throws SQLException {
        return null;
    }

    @Override
    public void delete(String id) throws UserNotFoundException {

    }

    public Page<Company> search(Integer page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(keyword) || keyword.isBlank()) {
            return companyRepo.findAll(pageable);
        }
        return companyRepo.findByKeyword(keyword, pageable);
    }
}
