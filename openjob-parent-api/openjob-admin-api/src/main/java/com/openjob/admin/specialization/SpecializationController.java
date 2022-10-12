package com.openjob.admin.specialization;

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

    @GetMapping(path = "/specializations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Specialization>> getAllSpecializations(){
        return ResponseEntity.ok(specializationService.getAll());
    }

    @PostMapping(path = "/specialization/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Specialization> createSpecialization(@RequestBody Specialization specialization) {
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
}
