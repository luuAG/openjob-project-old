package com.openjob.web.cv;

import com.openjob.common.model.CV;
import com.openjob.common.model.User;
import com.openjob.common.response.MessageResponse;
import com.openjob.web.dto.CVRequestDTO;
import com.openjob.web.job.JobService;
import com.openjob.web.jobcv.JobCvService;
import com.openjob.web.user.UserService;
import lombok.RequiredArgsConstructor;
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
        if (cv.isPresent())
            return ResponseEntity.ok(cv.get());
        return ResponseEntity.notFound().build();
    }

    @GetMapping(path = "/match-with-job/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> getUserHasCVmatchJob(@PathVariable("jobId") String jobId) {
        if (jobService.getById(jobId).isPresent()){
            return ResponseEntity.ok(userService.getByMatchingJob(jobId));
        }
        throw new IllegalArgumentException("Job not found for ID: "+jobId);
    }

    @PostMapping(path = "/create-update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CV> updateCV(@RequestBody CVRequestDTO requestCV) throws InvocationTargetException, IllegalAccessException {
        CV savedCV = cvService.saveUpdate(requestCV);
        if (Objects.nonNull(savedCV)){
            cvService.findJobMatchCV(savedCV); // async
            return ResponseEntity.ok(savedCV);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping(path = "/{cvId}/apply/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> applyCvForJob(@PathVariable("cvId") String cvId,
                                                         @PathVariable("jobId") String jobId) {
        jobCvService.saveNewApplication(cvId, jobId);
        return ResponseEntity.ok(new MessageResponse("Apply job successfully!"));
    }

    @DeleteMapping(path = "/{cvId}/remove-application/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> removeApplication(@PathVariable("cvId") String cvId,
                                                         @PathVariable("jobId") String jobId) {
        jobCvService.deleteApplication(cvId, jobId);
        return ResponseEntity.ok(new MessageResponse("Remove application successfully!"));
    }
}
