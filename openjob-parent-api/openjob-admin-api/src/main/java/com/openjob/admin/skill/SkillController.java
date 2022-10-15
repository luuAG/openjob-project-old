package com.openjob.admin.skill;

import com.openjob.admin.dto.JobPaginationDTO;
import com.openjob.admin.dto.NewSkillDTO;
import com.openjob.admin.job.JobService;
import com.openjob.admin.specialization.SpecializationService;
import com.openjob.common.model.Job;
import com.openjob.common.model.Skill;
import com.openjob.common.model.Specialization;
import com.openjob.common.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class SkillController {
    private final JobService jobService;
    private final SkillService skillService;
    private final SpecializationService specializationService;

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

    @GetMapping(path = "/skill/check-exist/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> checkSkillExist(@PathVariable("name") String name) {
        if (skillService.checkExistByName(name)){
            return ResponseEntity.badRequest().body(new MessageResponse("Skill exists"));
        }
        return ResponseEntity.ok(new MessageResponse("Accepted"));
    }

    @GetMapping(path = "/skill/byspecialization/{speId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Skill>> getBySpecialization(@PathVariable("speId") Integer speId){
        return ResponseEntity.ok(skillService.getBySpecialization(speId));
    }

    @PostMapping(path = "/skill/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Skill> createSkill(@RequestBody NewSkillDTO body){
        Skill skill = body.getSkill();
        skill.setSpecialization(specializationService.getById(body.getSpecializationId()));

        Skill savedSkill = skillService.save(skill);
        if (Objects.nonNull(savedSkill)){
            return ResponseEntity.ok(savedSkill);
        }
        return ResponseEntity.badRequest().body(null);
    }
    @PutMapping(path = "/skill/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Skill> updateSkill(@RequestBody Skill skill) {
        if (!skillService.checkExist(skill.getId()))
            throw new IllegalArgumentException("Skill does not exist");
        Skill savedSkill = skillService.save(skill);
        if (Objects.nonNull(savedSkill))
            return ResponseEntity.ok(savedSkill);
        return ResponseEntity.badRequest().body(null);
    }
}
