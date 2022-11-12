package com.openjob.web.specialization;

import com.openjob.common.model.Specialization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class SpecializationController {
    private final SpecializationService specializationService;



    @GetMapping(path = "/specialization/bymajor/{majorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Specialization>> getByMajor(@PathVariable("majorId") Integer majorId){
        return ResponseEntity.ok(specializationService.getByMajor(majorId));
    }
}
