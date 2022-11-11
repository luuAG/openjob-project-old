package com.openjob.web.job;

import com.openjob.common.model.Company;
import com.openjob.common.model.Job;
import com.openjob.common.model.Major;
import com.openjob.common.model.Specialization;
import com.openjob.common.response.MessageResponse;
import com.openjob.web.company.CompanyService;
import com.openjob.web.dto.JobPaginationDTO;
import com.openjob.web.dto.JobRequestDTO;
import com.openjob.web.major.MajorService;
import com.openjob.web.specialization.SpecializationService;
import com.openjob.web.util.NullAwareBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;
    private final CompanyService companyService;
    private final MajorService majorService;
    private final SpecializationService speService;

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobPaginationDTO> searchJob(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "companyId", required = false) String companyId) {
        Page<Job> pageJob = jobService.searchByKeywordAndLocationAndCompany(size, page, keyword, location, companyId);

        return ResponseEntity.ok(new JobPaginationDTO(
                pageJob.getContent(),
                pageJob.getTotalPages(),
                pageJob.getTotalElements()
        ));
    }

    @GetMapping(path = "/details/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Job> getJobDetails(@PathVariable("id") String id){
        Optional<Job> job = jobService.getById(id);
        if (job.isPresent())
            return ResponseEntity.ok(job.get());
        return ResponseEntity.notFound().build();

    }

    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> createNewJob(@RequestBody JobRequestDTO reqJob) throws InvocationTargetException, IllegalAccessException, SQLException {
        NullAwareBeanUtils beanCopier = NullAwareBeanUtils.getInstance();
        Job job = new Job();
        beanCopier.copyProperties(job, reqJob);

        Company company = companyService.getById(reqJob.getCompanyId());
        Optional<Major> major = majorService.getById(reqJob.getMajorId());
        Optional<Specialization> specialization = speService.getById(reqJob.getSpecializationId());
        if (Objects.isNull(company) || major.isEmpty() || specialization.isEmpty())
            throw new IllegalArgumentException("Company/Major/Specialization not found!");
        job.setCompany(company);
        job.setMajor(major.get());
        job.setSpecialization(specialization.get());

        Job savedJob = jobService.saveNewJob(job);
        if(Objects.nonNull(savedJob)){
            jobService.findCVmatchJob(savedJob); // async
            return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("New job is created successfully!"));
        }

        return ResponseEntity.badRequest().body(new MessageResponse("Creating job failed!"));
    }

    @DeleteMapping(path = "/delete/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> deleteJob(@PathVariable("jobId") String jobId){
        jobService.deleteById(jobId);
        return ResponseEntity.ok(new MessageResponse("Job is deleted!"));
    }



}
