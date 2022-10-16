package com.openjob.web.authentication;

import com.openjob.common.enums.AuthProvider;
import com.openjob.common.model.User;
import com.openjob.common.response.MessageResponse;
import com.openjob.web.dto.AuthResponseDTO;
import com.openjob.web.dto.LoginRequestDTO;
import com.openjob.web.dto.SignUpRequestDTO;
import com.openjob.web.exception.BadRequestException;
import com.openjob.web.security.user.service.TokenProvider;
import com.openjob.web.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignUpRequestDTO signUpRequest) {
        if(userService.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }

        // Creating user's account
        User user = new User();
        user.setFirstName(signUpRequest.getFirstname());
        user.setLastName(signUpRequest.getLastname());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());
        user.setAuthProvider(AuthProvider.DATABASE);


        User result = userService.save(user, true);
//
//        URI location = ServletUriComponentsBuilder
//                .fromCurrentContextPath().path("/user/me")
//                .buildAndExpand(result.getId()).toUri();

//        return ResponseEntity.created(location)
//                .body(new MessageResponse("User registered successfully!"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("User registered successfully!"));
    }

}
