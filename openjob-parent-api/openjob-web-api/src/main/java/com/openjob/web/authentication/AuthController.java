package com.openjob.web.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjob.common.enums.AuthProvider;
import com.openjob.common.model.User;
import com.openjob.common.response.MessageResponse;
import com.openjob.common.util.OpenJobUtils;
import com.openjob.web.config.security.service.TokenProvider;
import com.openjob.web.dto.LoginRequestDTO;
import com.openjob.web.dto.SignUpRequestDTO;
import com.openjob.web.exception.BadRequestException;
import com.openjob.web.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    @PostMapping("/login")
    public void authenticateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String bodyParams = OpenJobUtils.getParamsFromPost(request);
        LoginRequestDTO credential = new ObjectMapper().readValue(bodyParams, LoginRequestDTO.class);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credential.getEmail(),
                        credential.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        User user = userService.getByEmail(credential.getEmail());

        Map<String, String> responseDTO = new HashMap<>();
        responseDTO.put("access-token", accessToken);
        responseDTO.put("refresh-token", refreshToken);
        responseDTO.put("id", user.getId());
        responseDTO.put("role", user.getRole().name());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        new ObjectMapper().writeValue(response.getOutputStream(), responseDTO);
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
