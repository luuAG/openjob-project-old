package com.openjob.web.specialization;

import com.openjob.common.model.Specialization;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpecializationService {
    private final SpecializationRepository speRepo;

    public Optional<Specialization> getById(Integer id) {
        return speRepo.findById(id);
    }
}

