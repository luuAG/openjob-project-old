package com.openjob.web.job;

import com.openjob.common.model.Job;
import com.openjob.common.response.MessageResponse;
import com.openjob.web.company.CompanyService;
import com.openjob.web.dto.JobPaginationDTO;
import com.openjob.web.dto.JobRequestDTO;
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

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobPaginationDTO> searchJob(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam("keyword") String keyword,
            @RequestParam("location") String location) {
        Page<Job> pageJob = jobService.searchByKeywordAndLocation(size, page, keyword, location);

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

        job.setCompany(companyService.getById(reqJob.getCompanyId()));

        Job savedJob = jobService.saveNewJob(job);
        if(Objects.nonNull(savedJob))
            return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("New job is created successfully!"));
        return ResponseEntity.badRequest().body(new MessageResponse("Creating job failed!"));
    }

}
