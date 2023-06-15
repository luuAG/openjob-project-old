package com.openjob.web.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjob.common.model.Job;
import com.openjob.common.model.JobCV;
import com.openjob.common.model.PagingModel;
import com.openjob.common.model.User;
import com.openjob.common.response.MessageResponse;
import com.openjob.web.dto.*;
import com.openjob.web.jobcv.JobCvService;
import com.openjob.web.user.UserService;
import com.openjob.web.util.AuthenticationUtils;
import com.openjob.web.util.NullAwareBeanUtils;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.*;
import org.apache.commons.codec.binary.Base64;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;
    private final JobCvService jobCvService;
    private final AuthenticationUtils authenticationUtils;

    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobResponsePaginationDTO> searchJob(
//            @RequestParam("page") Integer page,
//            @RequestParam("size") Integer size,
//            @RequestParam(value = "keyword", required = false) String keyword,
//            @RequestParam(value = "location", required = false) String location,
//            @RequestParam(value = "companyId", required = false) String companyId,
            @Join(path = "jobSkills", alias = "jobSkill")
            @Join(path = "jobSkill.skill", alias = "skill")
            @Conjunction(
                value = {
                    @Or({@Spec(path = "title", params = "keyword", spec = Like.class),
                        @Spec(path = "company.name", params = "keyword", spec = Like.class),
                        @Spec(path = "skill.name", params = "keyword", spec = Like.class),}),
                    },
                and = {
                    @Spec(path = "company.id", params = "companyId",spec = Equal.class),
                    @Spec(path = "company.address", params = "location",spec = Equal.class)
                }
            )
            Specification<Job> jobSpec,
            PagingModel pagingModel,
            HttpServletRequest request) throws IOException {


        User loggedInUser = authenticationUtils.getLoggedInUser(request);

        List<JobResponseDTO> jobResponseDTOs = new ArrayList<>();
//        Page<Job> pageJob = jobService.searchByKeywordAndLocationAndCompany(size, page, keyword, location, companyId);
        Page<Job> jobPage = jobService.search(jobSpec, pagingModel.getPageable());
        // map job to dto
        jobPage.getContent().forEach(job -> {
            JobResponseDTO tempDto = jobService.mapJobToJobResponseDTO(job, loggedInUser);
            jobResponseDTOs.add(tempDto);
        });

        // get relevant jobs
        List<JobResponseDTO> relevantJobDtos = null;
        if (!jobPage.isEmpty()) {
            List<Job> relevantJobs = jobService.getRelevantJobs(jobPage.getContent().get(0));
            // map relevant jobs to dto
            relevantJobDtos = relevantJobs.stream()
                    .map(job -> jobService.mapJobToJobResponseDTO(job, loggedInUser))
                    .collect(Collectors.toList());
        }


        return ResponseEntity.ok(new JobResponsePaginationDTO(
                jobResponseDTOs,
                relevantJobDtos,
                jobPage.getTotalPages(),
                jobPage.getTotalElements()
        ));
    }

    @GetMapping(path = "/details/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobResponseDTO> getJobDetails(@PathVariable("id") String id, HttpServletRequest request) throws IOException {
        Optional<Job> job = jobService.getById(id);
        User finalLoggedInUser = authenticationUtils.getLoggedInUser(request);
        if (job.isPresent()) {
            JobResponseDTO toReturnDTO = jobService.mapJobToJobResponseDTO(job.get(), finalLoggedInUser);
            return ResponseEntity.ok(toReturnDTO);
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
    public ResponseEntity<JobCvPaginationDTO> getJobAppliedByUser(@PathVariable("userId")String userId,
                                                                  @RequestParam("page") Integer page,
                                                                  @RequestParam("size") Integer size){
        Page<JobCV> pageJobCv = jobService.getJobAppliedByUser(page, size, userId);

        return ResponseEntity.ok(new JobCvPaginationDTO(
                pageJobCv.getContent(),
                pageJobCv.getTotalPages(),
                pageJobCv.getTotalElements()
        ));
    }
    @PostMapping("/{jobId}/reset-expired-date")
    public ResponseEntity<MessageResponse> resetExpiredDate(@PathVariable("jobId")String jobId,
                                                            @RequestParam("expiredDate") Long expiredTimestamp) {
        Timestamp timestamp = new Timestamp(expiredTimestamp);
        jobService.setExpiredDate(jobId, new Date(timestamp.getTime()));
        return ResponseEntity.ok(new MessageResponse("Reset expired date successfully!"));
    }

    @GetMapping("/expired")
    public ResponseEntity<?> testGetExpiredJob(){
        return ResponseEntity.ok(jobService.getExpiredJob());
    }

    @GetMapping("/suggestion")
    public ResponseEntity<JobResponsePaginationDTO> getSuggestedJob(PagingModel pagingModel,
                                                                    HttpServletRequest request) throws IOException {
        User loggedInUser = authenticationUtils.getLoggedInUser(request);
        Page<Job> jobPage = jobService.getSuggestionJobs(pagingModel.getPageable(), loggedInUser);
        List<JobResponseDTO> jobResponseDTOList = jobPage.getContent().stream()
                .map(job -> jobService.mapJobToJobResponseDTO(job, loggedInUser))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new JobResponsePaginationDTO(
                jobResponseDTOList,
                jobPage.getTotalPages(),
                jobPage.getTotalElements()
        ));
    }

}
