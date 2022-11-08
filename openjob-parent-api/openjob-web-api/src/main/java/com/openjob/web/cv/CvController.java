package com.openjob.web.cv;

import com.openjob.common.model.CV;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cv")
public class CvController {
    private final CvService cvService;

    @GetMapping(path = "/byuserid/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CV> getCVbyUserId(@PathVariable("userId") String userId){
        Optional<CV> cv = cvService.getByUserId(userId);
        if (cv.isPresent())
            return ResponseEntity.ok(cv.get());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
