package com.openjob.admin.skill;

import com.openjob.admin.dto.JobPaginationDTO;
import com.openjob.admin.job.JobService;
import com.openjob.common.model.Job;
import com.openjob.common.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SkillController {
    private final JobService jobService;
    private final SkillService skillService;

    @GetMapping(path = "/skills-to-verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobPaginationDTO> getAllSkillsNotverified(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size){
        Page<Job> pageJob = jobService.getAllwithSkillnotverified(page, size);
        return ResponseEntity.ok(new JobPaginationDTO(
                pageJob.getContent(),
                pageJob.getTotalPages(),
                pageJob.getTotalElements()));
    }

    @PostMapping(path = "/verify-skill/{skillId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> verifySkill(@PathVariable("skillId") Integer skillId) {
        skillService.verifySkill(skillId);
        return ResponseEntity.ok(new MessageResponse("Skill is verified"));
    }

    @DeleteMapping(path = "/skill/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> deleteSkill(@PathVariable("id") Integer id){
        skillService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Skill is deleted"));
    }
}
