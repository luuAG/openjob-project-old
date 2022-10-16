package com.openjob.admin.specialization;

import com.openjob.admin.dto.NewSpecializationDTO;
import com.openjob.admin.major.MajorService;
import com.openjob.common.model.Specialization;
import com.openjob.common.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class SpecializationController {
    private final SpecializationService specializationService;
    private final MajorService majorService;

    @GetMapping(path = "/specializations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Specialization>> getAllSpecializations(){
        return ResponseEntity.ok(specializationService.getAll());
    }

    @PostMapping(path = "/specialization/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Specialization> createSpecialization(@RequestBody NewSpecializationDTO body) {
        Specialization specialization = body.getSpecialization();
        specialization.setMajor(majorService.getById(body.getMajorId()));
        Specialization savedSpecialization = specializationService.save(specialization);
        if (Objects.nonNull(savedSpecialization))
            return ResponseEntity.ok(savedSpecialization);
        return ResponseEntity.badRequest().body(null);
    }

    @PutMapping(path = "/specialization/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Specialization> updateSpecialization(@RequestBody Specialization specialization) {
        if (!specializationService.checkExist(specialization.getId()))
            throw new IllegalArgumentException("Specialization does not exist");
        Specialization savedSpecialization = specializationService.save(specialization);
        if (Objects.nonNull(savedSpecialization))
            return ResponseEntity.ok(savedSpecialization);
        return ResponseEntity.badRequest().body(null);
    }

    @DeleteMapping(path = "/specialization/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> deleteSpecialization(@PathVariable("id") Integer id){
        specializationService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Specilization is deleted"));
    }

    @GetMapping(path = "/specialization/check-exist/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> checkSpecializationExist(@PathVariable("name") String name) {
        if (specializationService.checkExistByName(name)){
            return ResponseEntity.badRequest().body(new MessageResponse("Major exists"));
        }
        return ResponseEntity.ok(new MessageResponse("Accepted"));
    }

    @GetMapping(path = "/specialization/bymajor/{majorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Specialization>> getByMajor(@PathVariable("majorId") Integer majorId){
        return ResponseEntity.ok(specializationService.getByMajor(majorId));
    }
}
