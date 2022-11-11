package com.openjob.web.cv;

import com.openjob.common.model.CV;
import com.openjob.web.dto.CVRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Objects;
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

    @PostMapping(path = "/create-update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CV> updateCV(@RequestBody CVRequestDTO requestCV) throws SQLException, InvocationTargetException, IllegalAccessException {
        CV savedCV = cvService.saveUpdate(requestCV);
        if (Objects.nonNull(savedCV)){
            cvService.findJobMatchCV(savedCV); // async
            return ResponseEntity.ok(savedCV);
        }

        return ResponseEntity.notFound().build();
    }
}
