package com.openjob.web.major;

import com.openjob.common.model.Major;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MajorService {
    private final MajorRepository majorRepo;

    public Optional<Major> getById(Integer id) {
        return majorRepo.findById(id);
    }

    public List<Major> getAll() {
        return majorRepo.findAll();
    }
}

