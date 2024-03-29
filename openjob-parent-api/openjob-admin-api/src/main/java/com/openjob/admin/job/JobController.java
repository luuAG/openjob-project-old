package com.openjob.admin.job;

import com.openjob.admin.dto.DeactivateJobDTO;
import com.openjob.admin.dto.JobPaginationDTO;
import com.openjob.admin.dto.ReviewJobDTO;
import com.openjob.common.model.Job;
import com.openjob.common.model.PagingModel;
import com.openjob.common.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.*;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Conjunction;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Join;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Or;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @GetMapping(path = "/jobs")
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
            PagingModel pagingModel) {
        Page<Job> pageJob = jobService.search(jobSpec, pagingModel.getPageable());
        return ResponseEntity.ok(new JobPaginationDTO(
                pageJob.getContent(),
                pageJob.getTotalPages(),
                pageJob.getTotalElements())
        );
    }

    @GetMapping(path = "/job/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Job> getById(@PathVariable("jobId") String jobId) {
        Optional<Job> job = jobService.getById(jobId);
        return job.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(path = "/job/review/{isApprove}")
    public ResponseEntity<MessageResponse> reviewJobs(@PathVariable("isApprove") Boolean isApprove, @RequestBody ReviewJobDTO reviewJobDTO){
        if (isApprove){
            jobService.approve(reviewJobDTO.getJobs());
        }else {
            jobService.reject(reviewJobDTO.getJobs(), reviewJobDTO.getRejectReasons());
        }
        return ResponseEntity.ok(new MessageResponse("Duyệt tin thành công!"));
    }

    @PostMapping(path = "/job/deactivate")
    public ResponseEntity<MessageResponse> deactivateJobs(@RequestBody DeactivateJobDTO deactivateJobDTO){
        jobService.deactivate(deactivateJobDTO.getJobId(), deactivateJobDTO.getReason());
        return ResponseEntity.ok(new MessageResponse("Đã vô hiệu hoá tin tuyển dụng"));
    }

    @PostMapping(path = "/job/activate")
    public ResponseEntity<MessageResponse> activateJobs(@RequestBody DeactivateJobDTO deactivateJobDTO){
        jobService.activate(deactivateJobDTO.getJobId(), deactivateJobDTO.getReason());
        return ResponseEntity.ok(new MessageResponse("Đã kích hoạt tin tuyển dụng"));
    }


}
