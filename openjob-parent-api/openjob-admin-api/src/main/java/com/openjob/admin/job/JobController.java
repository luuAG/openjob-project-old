package com.openjob.admin.job;

import com.openjob.admin.dto.JobPaginationDTO;
import com.openjob.common.model.Job;
import com.openjob.common.model.PagingModel;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.*;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Conjunction;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Or;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @GetMapping(path = "/jobs")
    public ResponseEntity<JobPaginationDTO> getAll(
            @Conjunction(
                    value = @Or({
                            @Spec(path = "title", params = "keyword", spec = Like.class),
                            @Spec(path = "company.name", params = "keyword", spec = Like.class)}),
                    and = {
                            @Spec(path = "createdAt", params = {"startDate", "endDate"}, spec = Between.class),
                            @Spec(path = "isActive", spec = Equal.class),
                            @Spec(path = "company.address", params = "address", spec = Like.class),
                            @Spec(path = "jobLevel", spec = Equal.class),
                            @Spec(path = "jobType", spec = Equal.class),
                            @Spec(path = "workplace", spec = Equal.class),
                            @Spec(path = "salaryInfo.min", params = "minSalary", spec = GreaterThanOrEqual.class),
                            @Spec(path = "salaryInfo.max", params = "maxSalary", spec = LessThanOrEqual.class),
                            @Spec(path = "salaryInfo.isNegotiable", params = "isSalaryNegotiable", spec = Equal.class),
                            @Spec(path = "salaryInfo.salaryType", params = "salaryType", spec = Equal.class),
                            @Spec(path = "major.id", params = "majorId", spec = Equal.class),
                            @Spec(path = "specialization.id", params = "speId", spec = Equal.class)
                    })
            Specification<Job> jobSpec,
            PagingModel pagingModel) {
        Page<Job> pageJob = jobService.search(jobSpec, pagingModel.getPageable());
        return ResponseEntity.ok(new JobPaginationDTO(
                pageJob.getContent(),
                pageJob.getTotalPages(),
                pageJob.getTotalElements())
        );
    }

//    @GetMapping(path = "/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<JobPaginationDTO> getAll(@RequestParam("page")Integer page,
//                                                   @RequestParam("size")Integer size,
//                                                   @RequestParam(value = "keyword", required = false)String keyword,
//                                                   @RequestParam(value = "majorId", required = false)Integer majorId,
//                                                   @RequestParam(value = "specializationId", required = false)Integer specializationId) {
//        Page<Job> jobPage = jobService.getAll(page, size, keyword, majorId, specializationId);
//        return ResponseEntity.ok(new JobPaginationDTO(
//                jobPage.getContent(),
//                jobPage.getTotalPages(),
//                jobPage.getTotalElements()));
//    }


//    @GetMapping(path = "/jobs", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<JobPaginationDTO> getAll(@RequestParam("page")Integer page,
//                                                   @RequestParam("size")Integer size,
//                                                   @RequestParam(value = "keyword", required = false)String keyword) {
//        Page<Job> jobPage = jobService.getAll(page, size, keyword);
//        return ResponseEntity.ok(new JobPaginationDTO(
//                jobPage.getContent(),
//                jobPage.getTotalPages(),
//                jobPage.getTotalElements()));
//    }

//    @GetMapping(path = "/job/by-company/{companyId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<JobPaginationDTO> getAll(@PathVariable("companyId") String companyId,
//                                                    @RequestParam("page")Integer page,
//                                                   @RequestParam("size")Integer size,
//                                                   @RequestParam(value = "keyword", required = false)String keyword) {
//        Page<Job> jobPage = jobService.getAllByCompanyId(page, size, keyword, companyId);
//        return ResponseEntity.ok(new JobPaginationDTO(
//                jobPage.getContent(),
//                jobPage.getTotalPages(),
//                jobPage.getTotalElements()));
//    }

    @GetMapping(path = "/job/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Job> getById(@PathVariable("jobId") String jobId) {
        Optional<Job> job = jobService.getById(jobId);
        return job.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


}
