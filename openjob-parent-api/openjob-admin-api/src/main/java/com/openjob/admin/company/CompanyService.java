package com.openjob.admin.company;

import com.openjob.admin.base.AbstractBaseService;
import com.openjob.admin.exception.UserNotFoundException;
import com.openjob.common.model.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
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
    public void delete(String id) throws UserNotFoundException {

    }
}
