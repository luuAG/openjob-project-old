package com.openjob.admin.skill;

import com.openjob.admin.dto.NewSkillDTO;
import com.openjob.admin.dto.SkillDTO;
import com.openjob.admin.dto.SkillPaginationDTO;
import com.openjob.admin.specialization.SpecializationService;
import com.openjob.common.model.CV;
import com.openjob.common.model.PagingModel;
import com.openjob.common.model.Skill;
import com.openjob.common.model.Specialization;
import com.openjob.common.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;
    private final SpecializationService specializationService;

    @GetMapping(path = "/skills-to-verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllSkillsNotverified(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size){
        Page<Skill> pageSkill = skillService.getNotVerified(page, size);
        List<SkillDTO> listSkillDTO = new ArrayList<>();
        pageSkill.getContent().forEach(skill -> {
            SkillDTO skillDTO = new SkillDTO();
            skillDTO.setId(skill.getId());
            skillDTO.setName(skill.getName());
            skillDTO.setIsVerified(skill.getIsVerified());
            skillDTO.setSpecialization(skill.getSpecialization().getName());
            skillDTO.setMajor(skill.getSpecialization().getMajor().getName());
            listSkillDTO.add(skillDTO);
        });
        Map<String, Object> response = new HashMap<>();
        response.put("listSkill", listSkillDTO);
        response.put("totalPages", pageSkill.getTotalPages());
        response.put("totalElements", pageSkill.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/verify-skills", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> verifySkill(@RequestBody Map<String, List<Integer>> map) {
        skillService.verifyManySkills(map.get("skillIds"));
        return ResponseEntity.ok(new MessageResponse("Skills are verified"));
    }

    @DeleteMapping(path = "/skill/delete/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> deleteSkill(@PathVariable("name") String name){
        skillService.deleteByName(name);
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
    public ResponseEntity<List<SkillDTO>> getBySpecialization(@PathVariable("speId") Integer speId){
        List<SkillDTO> result =  new ArrayList<>();

        skillService.getBySpecialization(speId).forEach(skill -> {
            SkillDTO skillDTO = new SkillDTO();
            skillDTO.setId(skill.getId());
            skillDTO.setName(skill.getName());
            skillDTO.setIsVerified(skill.getIsVerified());
            result.add(skillDTO);
        });
        return ResponseEntity.ok(result);
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
    @PostMapping(path = "/skill/update/{id}/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Skill> createSkill(@PathVariable Integer id, @PathVariable String name){
        Skill existingSKill = skillService.getById(id);
        existingSKill.setName(name);
        Skill savedSkill = skillService.save(existingSKill);
        if (Objects.nonNull(savedSkill)){
            return ResponseEntity.ok(savedSkill);
        }
        return ResponseEntity.badRequest().body(null);
    }
//    @PutMapping(path = "/skill/update", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Skill> updateSkill(@RequestBody Skill skill) {
//        if (!skillService.checkExist(skill.getId()))
//            throw new IllegalArgumentException("Skill does not exist");
//        skillService.save(skill);
//        if (Objects.nonNull(savedSkill))
//            return ResponseEntity.ok(savedSkill);
//        return ResponseEntity.badRequest().body(null);
//    }

    @GetMapping("/skills")
    public ResponseEntity<SkillPaginationDTO> getAllSkill(
            @And({
                    @Spec(path = "name", spec = Like.class),
                    @Spec(path = "specialization.id", params = "speId", spec = Equal.class)
            }) Specification<Skill> skillSpec, PagingModel pagingModel) {
        Page<Skill> pageCv = skillService.search(skillSpec, pagingModel.getPageable());
        return ResponseEntity.ok(new SkillPaginationDTO(
                pageCv.getContent(),
                pageCv.getTotalPages(),
                pageCv.getTotalElements())
        );
    }
}
