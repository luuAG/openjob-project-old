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
import net.kaczmarzyk.spring.data.jpa.domain.*;
import net.kaczmarzyk.spring.data.jpa.web.annotation.*;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Conjunction;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
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
            @Join(path = "jobSkills", alias = "js")
            @Join(path = "js.skill", alias = "skill")
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
                            @Spec(path = "workPlace", spec = Equal.class),
                            @Spec(path = "major.id", params = "majorId", spec = Equal.class),
                            @Spec(path = "specialization.id", params = "speId", spec = Equal.class),
                            @Spec(path = "salaryInfo.minSalary", params = "minSalary", spec = GreaterThanOrEqual.class),
                            @Spec(path = "salaryInfo.maxSalary", params = "maxSalary", spec = LessThanOrEqual.class),
                            @Spec(path = "salaryInfo.isSalaryNegotiable", params = "isSalaryNegotiable", spec = Equal.class),
                            @Spec(path = "salaryInfo.salaryType", params = "salaryType", spec = Equal.class),
                            @Spec(path = "jobStatus", spec = Equal.class),
                            @Spec(path = "skill.id", params = "skillId", spec = Equal.class)
                    })
            Specification<Job> jobSpec,
            PagingModel pagingModel,
            HttpServletRequest request) throws IOException {


        User loggedInUser = authenticationUtils.getLoggedInUser(request);

        List<JobResponseDTO> jobResponseDTOs = new ArrayList<>();
        Collection<JobResponseDTO> relevantJobDtos = new ArrayList<>();
        Page<Job> jobPage = jobService.search(jobSpec, pagingModel.getPageable());
        if (!jobPage.isEmpty()){
            // map job to dto
            jobResponseDTOs = jobPage.getContent().stream()
                    .map(job ->  jobService.mapJobToJobResponseDTO(job, loggedInUser))
                    .collect(Collectors.toList());
            // get relevant job dto
            relevantJobDtos = getRelevantJobs(jobPage.getContent().get(0), loggedInUser);
        }

        return ResponseEntity.ok(new JobResponsePaginationDTO(
                jobResponseDTOs,
                relevantJobDtos,
                jobPage.getTotalPages(),
                jobPage.getTotalElements()
        ));
    }

    private List<JobResponseDTO> getRelevantJobs(Job job, User loggedInUser){
        List<Job> relevantJobs = jobService.getRelevantJobs(job);
        // map  jobs to dto
        return relevantJobs.stream()
                .map(aJob -> jobService.mapJobToJobResponseDTO(aJob, loggedInUser))
                .collect(Collectors.toList());

    }

    @GetMapping(path = "/details/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobResponseDTO> getJobDetails(@PathVariable("id") String id, HttpServletRequest request) throws IOException {
        Optional<Job> job = jobService.getById(id);
        User finalLoggedInUser = authenticationUtils.getLoggedInUser(request);
        if (job.isPresent()) {
            JobResponseDTO toReturnDTO = jobService.mapJobToJobResponseDTO(job.get(), finalLoggedInUser);
            toReturnDTO.setRelevantJobs(getRelevantJobs(job.get(), finalLoggedInUser));
            return ResponseEntity.ok(toReturnDTO);
        }
        return ResponseEntity.notFound().build();

    }

//    @GetMapping(path = "/by-company/{companyId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<JobPaginationDTO> getJobByCompany(@PathVariable("companyId") String cId,
//                                                     @RequestParam("page") Integer page,
//                                                     @RequestParam("size") Integer size) {
//        Page<Job> jobPage = jobService.getByCompanyId(page, size, cId);
//        return ResponseEntity.ok(new JobPaginationDTO(
//                jobPage.getContent(),
//                jobPage.getTotalPages(),
//                jobPage.getTotalElements()
//        ));
//    }

    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> createNewJob(@RequestBody JobRequestDTO reqJob, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException, IOException {
        Job savedJob = jobService.saveUpdate(reqJob, request);
        if(Objects.nonNull(savedJob)){
            jobService.findCVmatchJob(savedJob); // async
            return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("New job is created successfully!"));
        }

        return ResponseEntity.badRequest().body(new MessageResponse("Creating job failed!"));
    }

    @PostMapping(path = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> updateJob(@RequestBody JobRequestDTO reqJob, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException, IOException {
        Job savedJob = jobService.saveUpdate(reqJob, request);
        if(Objects.nonNull(savedJob)){
//            jobService.findCVmatchJob(savedJob); // async
            return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Job is updated successfully!"));
        }

        return ResponseEntity.badRequest().body(new MessageResponse("Updating job failed!"));
    }

    @DeleteMapping(path = "/delete/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> deleteJob(@PathVariable("jobId") String jobId){
        jobService.deleteById(jobId);
        return ResponseEntity.ok(new MessageResponse("Job is deleted!"));
    }

    @GetMapping(path = "/applied-by-user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobCvPaginationDTO> getJobAppliedByUser(
            @PathVariable("userId")String userId,
            @Join(path = "job", alias = "job")
            @Conjunction(
                    value = @Or({
                            @Spec(path = "job.title", params = "keyword", spec = Like.class),
                            @Spec(path = "job.company.name", params = "keyword", spec = Like.class)
                    }),
                    and = {
                            @Spec(path = "applyDate", params = {"startDate", "endDate"}, spec = Between.class),
                            @Spec(path = "status", spec = Equal.class),
                            @Spec(path = "isApplied", constVal = "true", spec = Equal.class)
                    }
            ) Specification<JobCV> jobCvSpec,
            PagingModel pagingModel){
        jobCvSpec = jobCvSpec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("cv").get("user").get("id"), userId));
        Page<JobCV> pageJobCv = jobCvService.searchJobCv(jobCvSpec, pagingModel.getPageable());

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

    @GetMapping(path = "/by-company/{companyId}")
    public ResponseEntity<JobPaginationDTO> getAll(
            @Join(path = "jobSkills", alias = "js")
            @Join(path = "js.skill", alias = "skill")
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
                            @Spec(path = "workPlace", spec = Equal.class),
                            @Spec(path = "major.id", params = "majorId", spec = Equal.class),
                            @Spec(path = "specialization.id", params = "speId", spec = Equal.class),
                            @Spec(path = "salaryInfo.minSalary", params = "minSalary", spec = GreaterThanOrEqual.class),
                            @Spec(path = "salaryInfo.maxSalary", params = "maxSalary", spec = LessThanOrEqual.class),
                            @Spec(path = "salaryInfo.isSalaryNegotiable", params = "isSalaryNegotiable", spec = Equal.class),
                            @Spec(path = "salaryInfo.salaryType", params = "salaryType", spec = Equal.class),
                            @Spec(path = "jobStatus", spec = Equal.class),
                            @Spec(path = "skill.id", params = "skillId", spec = Equal.class)
                    })
            Specification<Job> jobSpec,
            PagingModel pagingModel,
            @PathVariable("companyId") String companyId) {

        Specification<Job> companyIdSpec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("company").get("id"), companyId);
        if (jobSpec == null)
            jobSpec = companyIdSpec;
        else
            jobSpec = jobSpec.and(companyIdSpec);
        Page<Job> pageJob = jobService.search(jobSpec, pagingModel.getPageable());
        return ResponseEntity.ok(new JobPaginationDTO(
                pageJob.getContent(),
                pageJob.getTotalPages(),
                pageJob.getTotalElements())
        );
    }
}
