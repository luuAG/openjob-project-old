package com.openjob.web.skill;

import com.openjob.common.model.PagingModel;
import com.openjob.common.model.Skill;
import com.openjob.web.job.JobService;
import com.openjob.web.specialization.SpecializationService;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.Collection;
import java.util.List;

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

    @GetMapping("/skills")
    public ResponseEntity<Collection<Skill>> getAllSkill() {
        return ResponseEntity.ok(skillService.getAll());
    }

}
