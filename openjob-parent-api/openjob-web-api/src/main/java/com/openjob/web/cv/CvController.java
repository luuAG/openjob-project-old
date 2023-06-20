package com.openjob.web.cv;

import com.openjob.common.model.CV;
import com.openjob.common.model.Company;
import com.openjob.common.model.JobCV;
import com.openjob.common.model.PagingModel;
import com.openjob.common.response.MessageResponse;
import com.openjob.web.dto.CVRequestDTO;
import com.openjob.web.dto.CvDTO;
import com.openjob.web.dto.CvPaginationDTO;
import com.openjob.web.dto.UserCvDto;
import com.openjob.web.job.JobService;
import com.openjob.web.jobcv.JobCvService;
import com.openjob.web.user.UserService;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cv")
public class CvController {
    private final CvService cvService;
    private final JobService jobService;
    private final UserService userService;
    private final JobCvService jobCvService;

    @GetMapping(path = "/byuserid/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CV> getCVbyUserId(@PathVariable("userId") String userId){
        Optional<CV> cv = cvService.getByUserId(userId);
        return cv.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

//    @GetMapping(path = "/match-with-job/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<UserCvDto>> getUserHasCVmatchJob(@PathVariable("jobId") String jobId) {
//        if (jobService.getById(jobId).isPresent()){
//            List<UserCvDto> users = userService.getByMatchingJob(jobId);
//            return ResponseEntity.ok(users);
//        }
//        throw new IllegalArgumentException("Job not found for ID: "+jobId);
//    }

    @PostMapping(path = "/create-update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CV> updateCV(@RequestBody CVRequestDTO requestCV) throws InvocationTargetException, IllegalAccessException {
        CV savedCV = cvService.saveUpdate(requestCV);
        if (Objects.nonNull(savedCV)){
//            cvService.findJobMatchCV(savedCV); // async
            return ResponseEntity.ok(savedCV);
        }

        return ResponseEntity.notFound().build();
    }

//    @GetMapping(path = "/applied-job/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Page<UserCvDto>> getCvAppliedJob (
//            @PathVariable("jobId") String jobId,
//            @RequestParam(value = "page", required = false) Integer page,
//            @RequestParam(value = "size", required = false) Integer size) {
//        if (jobService.getById(jobId).isPresent()){
//            List<UserCvDto> users = userService.getByJobApplied(jobId);
//            return ResponseEntity.ok(users);
//        }
//        throw new IllegalArgumentException("Job not found for ID: "+jobId);
//    }

    @PostMapping(path = "/{cvId}/apply/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> applyCvForJob(@PathVariable("cvId") String cvId,
                                                         @PathVariable("jobId") String jobId) {
        Optional<JobCV> jobCV = jobCvService.getByJobIdAndCvId(jobId, cvId);
        if (jobCV.isPresent() && jobCV.get().getIsApplied())
            throw new IllegalArgumentException("You have applied this job!");
        jobCvService.saveNewApplication(cvId, jobId);
        return ResponseEntity.ok(new MessageResponse("Apply job successfully!"));
    }

    @DeleteMapping(path = "/{cvId}/remove-application/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> removeApplication(@PathVariable("cvId") String cvId,
                                                         @PathVariable("jobId") String jobId) {
        jobCvService.deleteApplication(cvId, jobId);
        return ResponseEntity.ok(new MessageResponse("Remove application successfully!"));
    }

    @GetMapping()
    public ResponseEntity<CvPaginationDTO> searchCv(
        @Join(path = "listSkill", alias = "skill")
        @Conjunction(
                value =
                    @Or({
                        @Spec(path = "education", params = "keyword", spec = Like.class),
                        @Spec(path = "experience", params = "keyword", spec = Like.class),
                        @Spec(path = "certificate", params = "keyword", spec = Like.class),
                        @Spec(path = "major.id", params = "majorId", spec = Equal.class),
                        @Spec(path = "specialization.id", params = "specializationId", spec = Equal.class),
                    }),
                and = {
                    @Spec(path = "skill.id", params = "skill1", spec = Equal.class),
                    @Spec(path = "skill.id", params = "skill2", spec = Equal.class),
                    @Spec(path = "skill.id", params = "skill3", spec = Equal.class),
                    @Spec(path = "skill.id", params = "skill4", spec = Equal.class),
                    @Spec(path = "skill.id", params = "skill5", spec = Equal.class),
                }
        ) Specification<CV> cvSpec, PagingModel pagingModel){
        Page<CV> pageCv = cvService.search(cvSpec, pagingModel.getPageable());
        List<CvDTO> cvDTOs = cvService.mapToCvDto(pageCv.getContent());
        return ResponseEntity.ok(new CvPaginationDTO(
                cvDTOs,
                pageCv.getTotalPages(),
                pageCv.getTotalElements())
        );
    }
}
