package com.openjob.admin.webuser;

import com.openjob.admin.dto.UserPaginationDTO;
import com.openjob.admin.exception.UserNotFoundException;
import com.openjob.common.model.User;
import com.openjob.common.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class WebUserController {

    private final WebUserService userService;


    @GetMapping(path = "/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUser(@PathVariable("id") String id) {
        if (Objects.isNull(id)){
            throw new IllegalArgumentException("ID is null");
        }
        Optional<User> userOptional = userService.get(id);
        if (userOptional.isPresent())
            return ResponseEntity.ok(userOptional.get());
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserPaginationDTO> getUsers(
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean byCompany){
        Page<User> pageUser;
        if (Objects.nonNull(byCompany) && byCompany)
            pageUser = userService.searchByCompany(page, size, keyword);
        else
            pageUser = userService.searchByKeyword(page, size, keyword);
        return ResponseEntity.ok(new UserPaginationDTO(
                pageUser.getContent(),
                pageUser.getTotalPages(),
                pageUser.getTotalElements()
        ));
    }

    @PostMapping(path = "/user/activate/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> activateUser(@PathVariable String id) throws UserNotFoundException, SQLException {
        Optional<User> optionalWebUser = userService.get(id);
        if (optionalWebUser.isPresent()){
            User webUser = optionalWebUser.get();
            webUser.setIsActive(true);
            userService.saveWithoutPassword(webUser);
        } else {
            throw new UserNotFoundException("HR user not found with ID: " + id);
        }
        return ResponseEntity.ok(new MessageResponse("HR user is activated, ID: " + id));
    }

    @DeleteMapping(path = "/user/deactivate/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> deactivateHr(@PathVariable String id) throws UserNotFoundException, SQLException {
        Optional<User> optionalWebUser = userService.get(id);
        if (optionalWebUser.isPresent()){
            User webUser = optionalWebUser.get();
            webUser.setIsActive(false);
            userService.saveWithoutPassword(webUser);
        } else {
            throw new UserNotFoundException("HR user not found with ID: " + id);
        }
        return ResponseEntity.ok(new MessageResponse("HR user is deactivated, ID: " + id));
    }




}
