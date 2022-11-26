package com.openjob.web.jobcv;

import com.openjob.common.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jobcv")
public class JobCVController {
    private final JobCvService jobCvService;

    @PostMapping(path = "/accept/{jobId}/{cvId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> acceptCV(
            @PathVariable("jobId") String jobId,
            @PathVariable("cvId") String cvId) {
        jobCvService.acceptCV(jobId, cvId);
        return ResponseEntity.ok(new MessageResponse("CV has been accepted!"));
    }

    @PostMapping(path = "/reject/{jobId}/{cvId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> rejectCV(
            @PathVariable("jobId") String jobId,
            @PathVariable("cvId") String cvId) {
        jobCvService.rejectCV(jobId, cvId);
        return ResponseEntity.ok(new MessageResponse("CV has been rejected!"));
    }
}
