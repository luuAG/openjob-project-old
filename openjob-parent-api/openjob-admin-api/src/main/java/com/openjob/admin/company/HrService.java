package com.openjob.admin.company;

import com.openjob.common.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HrService {
    private final HrRepository hrRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public void activate(String companyId){
        hrRepo.activate(companyId);
    }

    public void deactivate(String companyId){
        hrRepo.deactivate(companyId);
    }

    public User getByCompany(String companyId) {
        Optional<User> optionalUser = hrRepo.findByCompany(companyId);
        if (optionalUser.isPresent())
            return optionalUser.get();
        else
            throw new IllegalArgumentException("HR not found for company ID: " + companyId);
    }

    public User create(User hr) {
        try {
            hr.setPassword(passwordEncoder.encode(hr.getPassword()));
            return hrRepo.save(hr);
        } catch (DataIntegrityViolationException e){
            throw new DataIntegrityViolationException(NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        }
    }

    public User update(User hr, Boolean updatePassword) {
        User existingHr = hrRepo.getById(hr.getId());
        if (updatePassword)
            hr.setPassword(passwordEncoder.encode(hr.getPassword()));
        return hrRepo.save(hr);
    }

    public void delete(User hr) {
        hrRepo.delete(hr);
    }
}
