package com.openjob.admin.job;

import com.openjob.admin.company.CompanyService;
import com.openjob.common.model.Job;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepo;
    private final CompanyService companyRepo;

    public Page<Job> getAll(Integer page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(keyword) || keyword.isBlank()) {
            return jobRepo.findAll(pageable);
        }
        return jobRepo.findAllWithKeyword(keyword, pageable);
    }

    public Page<Job> getAllByCompanyId(Integer page, Integer size, String keyword, String companyId) {
        Pageable pageable = PageRequest.of(page, size);
        if (Objects.isNull(keyword) || keyword.isBlank()) {
            return jobRepo.findAllByCompanyId(companyId, pageable);
        }
        return jobRepo.findAllByCompanyIdWithKeyword(companyId, keyword, pageable);
    }

    public Optional<Job> getById(String jobId) {
        return jobRepo.findById(jobId);
    }

//    public Page<Job> getAllwithSkillnotverified(Integer page, Integer size) {
//        Pageable pageable = PageRequest.of(page, size);
//        return jobRepo.findAllwithSkillnotVerified(pageable);
//    }

}
