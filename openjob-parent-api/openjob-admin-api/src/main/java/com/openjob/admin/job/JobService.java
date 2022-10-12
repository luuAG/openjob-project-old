package com.openjob.admin.job;

import com.openjob.common.model.Job;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepo;

    public Page<Job> getAllwithSkillnotverified(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return jobRepo.findAllwithSkillnotVerified(pageable);
    }
}
