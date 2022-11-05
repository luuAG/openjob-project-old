package com.openjob.web.user;

import com.openjob.common.model.User;
import com.openjob.web.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping(path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUserInfo(@PathVariable("userId") String userId) {
        Optional<User> user = userService.get(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return ResponseEntity.ok(user.get());
    }

    @PatchMapping(path = "/userprofile/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUserInfo(@RequestBody User userInfo) throws InvocationTargetException, IllegalAccessException {
        if (Objects.isNull(userInfo.getId())){
            throw new IllegalArgumentException("ID is null");
        }
        if (userService.existById(userInfo.getId())){
            throw new ResourceNotFoundException("User", "id", userInfo.getId());
        }
        User updatedUser = userService.patchUpdate(userInfo);
        return ResponseEntity.ok(updatedUser);
    }
}
