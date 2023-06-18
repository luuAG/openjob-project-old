package com.openjob.admin.job;

import com.openjob.common.model.Job;
import com.openjob.common.model.SalaryModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobService {
    private final JobRepository jobRepo;
    private final Sort sort = Sort.by(Sort.Direction.DESC,"createdAt");

//    public Page<Job> getAll(Integer page, Integer size, String keyword, Integer majorId, Integer specializationId) {
//        Pageable pageable = PageRequest.of(page, size, sort);
//        List<Job> results;
//        if (Objects.isNull(keyword) || keyword.isBlank()) {
//            results = jobRepo.findAll(pageable).getContent();
//        } else {
//            results = jobRepo.findAllWithKeyword(keyword, pageable).getContent();
//        }
//
//        if (Objects.nonNull(majorId)) {
//            results = results.stream()
//                    .filter(job -> Objects.equals(job.getSpecialization().getMajor().getId(), majorId))
//                    .collect(Collectors.toList());
//        }
//        if (Objects.nonNull(specializationId)) {
//            results = results.stream()
//                    .filter(job -> Objects.equals(job.getSpecialization().getId(), specializationId))
//                    .collect(Collectors.toList());
//        }
//        return new PageImpl<>(results);
//    }
//
//    public Page<Job> getAllByCompanyId(Integer page, Integer size, String keyword, String companyId) {
//        Pageable pageable = PageRequest.of(page, size, sort);
//        if (Objects.isNull(keyword) || keyword.isBlank()) {
//            return jobRepo.findAllByCompanyId(companyId, pageable);
//        }
//        return jobRepo.findAllByCompanyIdWithKeyword(companyId, keyword, pageable);
//    }

    public Optional<Job> getById(String jobId) {
        return jobRepo.findById(jobId);
    }

    public Page<Job> search(Specification<Job> jobSpec, Pageable pageable) {
        return jobRepo.findAll(jobSpec, pageable);
    }

    public void approve(List<Job> jobs) {
        List<String> ids = jobs.stream().map(Job::getId).collect(Collectors.toList());
        jobRepo.approveByIds(ids);
    }

    public void reject(List<Job> jobs, List<String> rejectReasons) {
        List<String> ids = jobs.stream().map(Job::getId).collect(Collectors.toList());
        jobRepo.rejectByIds(ids);
        // TODO: send mail about the reason
    }


//    public Page<Job> getAllwithSkillnotverified(Integer page, Integer size) {
//        Pageable pageable = PageRequest.of(page, size);
//        return jobRepo.findAllwithSkillnotVerified(pageable);
//    }

}
