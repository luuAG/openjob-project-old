package com.openjob.web.cv;

import com.openjob.common.model.*;
import com.openjob.common.response.MessageResponse;
import com.openjob.web.dto.CVRequestDTO;
import com.openjob.web.dto.UserCVwithExtraDataDTO;
import com.openjob.web.dto.CvDTO;
import com.openjob.web.dto.CvPaginationDTO;
import com.openjob.web.jobcv.JobCvService;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cv")
public class CvController {
    private final CvService cvService;
    private final JobCvService jobCvService;

    @GetMapping(path = "/byuserid/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CV> getCVbyUserId(@PathVariable("userId") String userId){
        Optional<CV> cv = cvService.getByUserId(userId);
        return cv.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/match-with-job/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<Page<UserCVwithExtraDataDTO>> getCvMatchedJob(
            @PathVariable("jobId") final String jobId,
            @Join(path = "cv", alias = "cv")
            @Join(path = "cv.skills", alias = "cvSkill")
            @Join(path = "cvSkill.skill", alias = "skill")
            @Conjunction(
                    value =
                    @Or({
                            @Spec(path = "cv.education", params = "keyword", spec = Like.class),
                            @Spec(path = "cv.experience", params = "keyword", spec = Like.class),
                            @Spec(path = "cv.certificate", params = "keyword", spec = Like.class),
                            @Spec(path = "cv.major.id", params = "majorId", spec = Equal.class),
                            @Spec(path = "cv.specialization.id", params = "specializationId", spec = Equal.class),
                    }),
                    and = {
                            @Spec(path = "skill.id", params = "skillId", spec = Equal.class),
                            @Spec(path = "isMatched", constVal = "true", spec = Equal.class)
                    }
            ) Specification<JobCV> jobCvSpec, PagingModel pagingModel){
        jobCvSpec = jobCvSpec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("job").get("id"), jobId));

        Page<JobCV> jobCVPage = jobCvService.searchJobCv(jobCvSpec, pagingModel.getPageable());
//        List<CvDTO> cvDTOs = cvService.mapToCvDto(jobCVPage.getContent());
        List<UserCVwithExtraDataDTO> results = jobCVPage.getContent().stream()
                .map(item ->  {
                    UserCVwithExtraDataDTO temp = new UserCVwithExtraDataDTO();
                    temp.setUser(item.getCv().getUser());
                    return temp;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(new PageImpl<>(results));
    }

    @PostMapping(path = "/create-update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CV> updateCV(@RequestBody CVRequestDTO requestCV, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException, IOException {
        CV savedCV = cvService.saveUpdate(requestCV, request);
        if (Objects.nonNull(savedCV)){
//            cvService.findJobMatchCV(savedCV); // async
            return ResponseEntity.ok(savedCV);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping(path = "/applied-job/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UserCVwithExtraDataDTO>> getCvAppliedJob(
            @PathVariable("jobId") final String jobId,
            @Join(path = "cv", alias = "cv")
            @Join(path = "cv.skills", alias = "cvSkill")
            @Join(path = "cvSkill.skill", alias = "skill")
            @Conjunction(
                    value =
                    @Or({
                            @Spec(path = "cv.education", params = "keyword", spec = Like.class),
                            @Spec(path = "cv.experience", params = "keyword", spec = Like.class),
                            @Spec(path = "cv.certificate", params = "keyword", spec = Like.class),
                            @Spec(path = "cv.major.id", params = "majorId", spec = Equal.class),
                            @Spec(path = "cv.specialization.id", params = "specializationId", spec = Equal.class),
                    }),
                    and = {
                            @Spec(path = "skill.id", params = "skillId", spec = Equal.class),
                            @Spec(path = "isApplied", constVal = "true", spec = Equal.class)
                    }
            ) Specification<JobCV> jobCvSpec, PagingModel pagingModel){

        jobCvSpec = jobCvSpec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("job").get("id"), jobId));

        Page<JobCV> jobCVPage = jobCvService.searchJobCv(jobCvSpec, pagingModel.getPageable());
//        List<CvDTO> cvDTOs = cvService.mapToCvDto(jobCVPage.getContent());
        List<UserCVwithExtraDataDTO> results = jobCVPage.getContent().stream()
                .map(item ->  {
                    UserCVwithExtraDataDTO temp = new UserCVwithExtraDataDTO();
                    temp.setUser(item.getCv().getUser());
                    return temp;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(new PageImpl<>(results));

    }

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
        @Join(path = "skills", alias = "cvSkill")
        @Join(path = "cvSkill.skill", alias = "skill")
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
                    @Spec(path = "skill.id", params = "skillId", spec = Equal.class),
                    @Spec(path = "isActive", constVal = "true", spec = Equal.class)
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

    @GetMapping("/{cvId}/view-by-company/{companyId}")
    public ResponseEntity<UserCVwithExtraDataDTO> companyViewCv(@PathVariable("cvId") String cvId, @PathVariable("companyId") String companyId) {
        return ResponseEntity.ok(cvService.getCvForCompanyView(cvId, companyId));
    }

    @PostMapping("/{cvId}/charge-company/{companyId}/{isFree}")
    public ResponseEntity<MessageResponse> chargeCompanyForViewCv(@PathVariable("cvId") String cvId,
                                                                  @PathVariable("companyId") String companyId,
                                                                  @PathVariable("isFree") Boolean isFree){
        cvService.chargeCompanyForViewCv(cvId, companyId, isFree);
        return ResponseEntity.ok(new MessageResponse("Thanh toán thành công!"));
    }
}
