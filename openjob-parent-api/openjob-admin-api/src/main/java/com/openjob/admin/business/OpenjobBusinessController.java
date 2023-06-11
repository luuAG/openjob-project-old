package com.openjob.admin.business;


import com.openjob.common.model.OpenjobBusiness;
import com.openjob.common.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/business")
@RequiredArgsConstructor
public class OpenjobBusinessController {
    private final OpenjobBusinessService service;

    @GetMapping
    public ResponseEntity<OpenjobBusiness> getBusiness() {
        return ResponseEntity.ok(service.get());
    }
    @PostMapping
    public ResponseEntity<MessageResponse> updateBusiness(@RequestBody OpenjobBusiness openjobBusiness) {
        service.update(openjobBusiness);
        return ResponseEntity.ok(new MessageResponse("Cập nhật thành công"));
    }

}

