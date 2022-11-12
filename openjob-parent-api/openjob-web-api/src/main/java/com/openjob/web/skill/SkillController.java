package com.openjob.web.skill;

import com.openjob.common.model.Skill;
import com.openjob.web.job.JobService;
import com.openjob.web.specialization.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class SkillController {
    private final JobService jobService;
    private final SkillService skillService;
    private final SpecializationService specializationService;


    @GetMapping(path = "/skill/by-specialization/{speId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Skill>> getBySpecialization(@PathVariable("speId") Integer speId){
        return ResponseEntity.ok(skillService.getBySpecialization(speId));
    }

}
