package com.openjob.web.cv;

import com.openjob.common.model.CV;
import com.openjob.common.model.User;
import com.openjob.common.response.MessageResponse;
import com.openjob.web.dto.CVRequestDTO;
import com.openjob.web.dto.CvDTO;
import com.openjob.web.dto.CvPaginationDTO;
import com.openjob.web.dto.UserCvDto;
import com.openjob.web.job.JobService;
import com.openjob.web.jobcv.JobCvService;
import com.openjob.web.user.UserService;
import com.openjob.web.util.NullAwareBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
    public ResponseEntity<List<UserCvDto>> getUserHasCVmatchJob(@PathVariable("jobId") String jobId) {
        if (jobService.getById(jobId).isPresent()){
            List<User> users = userService.getByMatchingJob(jobId);
            List<UserCvDto> result = new ArrayList<>();
            users.forEach(user -> {
                NullAwareBeanUtils copier = NullAwareBeanUtils.getInstance();
                UserCvDto userCvDto = new UserCvDto();
                try {
                    copier.copyProperties(userCvDto, user);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                userCvDto.setCvStatus(jobCvService.getStatus(jobId, user.getCv().getId()));
                result.add(userCvDto);
            });
            return ResponseEntity.ok(result);
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

    @GetMapping(path = "/applied-job/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CvPaginationDTO> getCvAppliedJob (
            @PathVariable("jobId") String jobId,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size) {
        NullAwareBeanUtils copier = NullAwareBeanUtils.getInstance();
        Page<CV> pageCV = cvService.getByJobId(page, size, jobId);
        List<CvDTO> listCV = new ArrayList<>();
        pageCV.getContent().forEach(cv -> {
            CvDTO cvDTO = new CvDTO();
            try {
                copier.copyProperties(cvDTO, cv);
                cvDTO.setUserId(cv.getUser().getId());
                cvDTO.setStatus(jobCvService.getStatus(jobId, cv.getId()));
                listCV.add(cvDTO);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
        return ResponseEntity.ok(new CvPaginationDTO(
                listCV,
                pageCV.getTotalPages(),
                pageCV.getTotalElements()
        ));
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
