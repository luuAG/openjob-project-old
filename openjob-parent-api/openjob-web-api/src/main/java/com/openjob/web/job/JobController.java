package com.openjob.web.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjob.common.model.Job;
import com.openjob.common.model.User;
import com.openjob.common.response.MessageResponse;
import com.openjob.web.dto.JobPaginationDTO;
import com.openjob.web.dto.JobRequestDTO;
import com.openjob.web.dto.JobResponseDTO;
import com.openjob.web.dto.JobResponsePaginationDTO;
import com.openjob.web.jobcv.JobCvService;
import com.openjob.web.user.UserService;
import com.openjob.web.util.NullAwareBeanUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;
    private final UserService userService;
    private final JobCvService jobCvService;

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobResponsePaginationDTO> searchJob(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "companyId", required = false) String companyId,
            HttpServletRequest request) throws IOException {
        String email = null;
        String accessToken = request.getHeader("authorization");
        User loggedInUser=null;
        if (Objects.nonNull(accessToken)){
            String payloadJWT = accessToken.split("\\.")[1];
            Base64 base64 = new Base64(true);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> info = mapper.readValue(base64.decode(payloadJWT), Map.class);
            email = info.get("sub");
            loggedInUser = userService.getByEmail(email);
        }

        List<JobResponseDTO> results = new ArrayList<>();

        Page<Job> pageJob = jobService.searchByKeywordAndLocationAndCompany(size, page, keyword, location, companyId);
        User finalLoggedInUser = loggedInUser;
        pageJob.getContent().forEach(job -> {
            JobResponseDTO tempDto = new JobResponseDTO();
            NullAwareBeanUtils copier = NullAwareBeanUtils.getInstance();
            try {
                copier.copyProperties(tempDto, job);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            if (Objects.nonNull(finalLoggedInUser))
                tempDto.setIsApplied(jobCvService.checkUserAppliedJob(finalLoggedInUser.getId(),job.getId()));
            results.add(tempDto);
        });
        PageImpl<JobResponseDTO> jobPage = new PageImpl<>(results);
        return ResponseEntity.ok(new JobResponsePaginationDTO(
                jobPage.getContent(),
                jobPage.getTotalPages(),
                jobPage.getTotalElements()
        ));
    }

    @GetMapping(path = "/details/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobResponseDTO> getJobDetails(@PathVariable("id") String id, HttpServletRequest request) throws IOException {
        String email = null;
        String accessToken = request.getHeader("authorization");
        User loggedInUser=null;
        if (Objects.nonNull(accessToken)){
            String payloadJWT = accessToken.split("\\.")[1];
            Base64 base64 = new Base64(true);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> info = mapper.readValue(base64.decode(payloadJWT), Map.class);
            email = info.get("sub");
            loggedInUser = userService.getByEmail(email);
        }
        Optional<Job> job = jobService.getById(id);
        User finalLoggedInUser = loggedInUser;
        if (job.isPresent()) {
            JobResponseDTO tempDto = new JobResponseDTO();
            NullAwareBeanUtils copier = NullAwareBeanUtils.getInstance();
            try {
                copier.copyProperties(tempDto, job.get());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            if (Objects.nonNull(finalLoggedInUser))
                tempDto.setIsApplied(jobCvService.checkUserAppliedJob(finalLoggedInUser.getId(),job.get().getId()));
            return ResponseEntity.ok(tempDto);
        }

        return ResponseEntity.notFound().build();

    }

    @GetMapping(path = "/by-company/{companyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobPaginationDTO> getJobByCompany(@PathVariable("companyId") String cId,
                                                     @RequestParam("page") Integer page,
                                                     @RequestParam("size") Integer size) {
        Page<Job> jobPage = jobService.getByCompanyId(page, size, cId);
        return ResponseEntity.ok(new JobPaginationDTO(
                jobPage.getContent(),
                jobPage.getTotalPages(),
                jobPage.getTotalElements()
        ));
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

    @GetMapping(path = "/applied-by-user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobPaginationDTO> getJobAppliedByUser(@PathVariable("userId")String userId,
                                                                @RequestParam("page") Integer page,
                                                                @RequestParam("size") Integer size){
        Page<Job> pageJob = jobService.getJobAppliedByUser(page, size, userId);

        return ResponseEntity.ok(new JobPaginationDTO(
                pageJob.getContent(),
                pageJob.getTotalPages(),
                pageJob.getTotalElements()
        ));
    }


}
