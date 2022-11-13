package com.openjob.web.job;

import com.openjob.common.model.Job;
import com.openjob.common.response.MessageResponse;
import com.openjob.web.dto.JobPaginationDTO;
import com.openjob.web.dto.JobRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

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

    @GetMapping(path = "/by-company/{companyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Job>> getJobByCompany(@PathVariable("companyId") String cId) {
        return ResponseEntity.ok(jobService.getByCompanyId(cId));
    }

    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> createNewJob(@RequestBody JobRequestDTO reqJob) throws InvocationTargetException, IllegalAccessException {
        Job savedJob = jobService.saveNewJob(reqJob);
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
