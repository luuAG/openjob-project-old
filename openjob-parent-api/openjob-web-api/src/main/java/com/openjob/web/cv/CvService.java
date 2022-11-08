package com.openjob.web.cv;

import com.openjob.common.model.CV;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CvService {
    private final CvRepository cvRepo;

    public Optional<CV> getById(String id){
        return cvRepo.findById(id);
    }

    public Optional<CV> getByUserId(String userId){
        return cvRepo.findByUserId(userId);
    }

    public CV saveUpdate(CV cv){
        return cvRepo.save(cv);
    }
}
