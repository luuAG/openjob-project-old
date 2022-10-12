package com.openjob.admin.major;

import com.openjob.common.model.Major;
import com.openjob.common.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class MajorController {
    private final MajorService majorService;

    @GetMapping(path = "/majors", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Major>> getAllMajors(){
        return ResponseEntity.ok(majorService.getAll());
    }

    @PostMapping(path = "/major/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Major> createMajor(@RequestBody Major major) {
        Major savedMajor = majorService.save(major);
        if (Objects.nonNull(savedMajor))
            return ResponseEntity.ok(savedMajor);
        return ResponseEntity.badRequest().body(null);
    }

    @PutMapping(path = "/major/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Major> updateMajor(@RequestBody Major major) {
        if (!majorService.checkExist(major.getId()))
            throw new IllegalArgumentException("Major does not exist");
        Major savedMajor = majorService.save(major);
        if (Objects.nonNull(savedMajor))
            return ResponseEntity.ok(savedMajor);
        return ResponseEntity.badRequest().body(null);
    }

    @DeleteMapping(path = "/major/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> deleteMajor(@PathVariable("id") Integer id){
        majorService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Major is deleted"));
    }
}
