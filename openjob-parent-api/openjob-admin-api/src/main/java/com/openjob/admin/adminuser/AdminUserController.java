package com.openjob.admin.adminuser;

import com.openjob.admin.exception.AdminUserNotFound;
import com.openjob.common.model.Admin;
import com.openjob.common.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService adminUserService;

    @GetMapping(path = "/adminuser/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Admin> getAdminUser(@PathVariable final String id) throws AdminUserNotFound {
        if (Objects.isNull(id) || id.isEmpty()){
            throw new IllegalArgumentException("ID is null or empty");
        }
        Optional<Admin> admin = adminUserService.get(id);
        if (admin.isPresent())
            return ResponseEntity.ok(admin.get());
        throw new AdminUserNotFound("Admin user not found for ID: " + id);
    }

    @GetMapping(path = "/adminusers/page/{pageNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Admin>> getAdminUserByPage(
            @PathVariable final Integer pageNumber,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive){
        return ResponseEntity.ok(adminUserService.searchByPage(keyword, pageNumber, isActive));
    }

    @PostMapping(path = "/adminuser/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Admin> createAdminUser(@Valid @RequestBody final Admin admin) {
        if (Objects.isNull(admin)){
            throw new IllegalArgumentException("Object is null");
        }
        Admin savedAdmin = adminUserService.save(admin);
        savedAdmin.setPassword("hidden-for-security");
        return new ResponseEntity<>(savedAdmin, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/adminuser/deactivate/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> deactivateAdminUser(@PathVariable final String id) throws AdminUserNotFound {
        if (Objects.isNull(id)){
            throw new IllegalArgumentException("ID is null");
        }
        adminUserService.deactivate(id);

        return ResponseEntity.ok(new MessageResponse("Admin user with ID: " + id + " is deactivated"));
    }

    @PostMapping(path = "/adminuser/activate/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> activateAdminUser(@PathVariable final String id) throws AdminUserNotFound {
        if (Objects.isNull(id)){
            throw new IllegalArgumentException("ID is null");
        }
        adminUserService.activate(id);

        return ResponseEntity.ok(new MessageResponse("Admin user with ID: " + id + " is activated"));
    }



}
