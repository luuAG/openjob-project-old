package com.openjob.web.major;

import com.openjob.common.model.Major;
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
public class MajorController {
    private final MajorService majorService;

    @GetMapping(path = "/majors", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Major>> getAllMajors(){
        return ResponseEntity.ok(majorService.getAll());
    }

}
