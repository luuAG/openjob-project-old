package com.openjob.admin.adminuser;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjob.admin.config.ConfigProperty;
import com.openjob.admin.dto.AdminPaginationDTO;
import com.openjob.admin.exception.UserNotFoundException;
import com.openjob.common.model.Admin;
import com.openjob.common.response.ErrorResponse;
import com.openjob.common.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService adminUserService;
    private final ConfigProperty configProperties;

    @GetMapping(path = "/adminuser/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Admin> getAdminUser(@PathVariable final String id) throws UserNotFoundException {
        if (Objects.isNull(id) || id.isEmpty()){
            throw new IllegalArgumentException("ID is null or empty");
        }
        Optional<Admin> admin = adminUserService.get(id);
        if (admin.isPresent())
            return ResponseEntity.ok(admin.get());
        throw new UserNotFoundException("Admin user not found for ID: " + id);
    }

    @GetMapping(path = "/adminusers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminPaginationDTO> getAdminUserByPage(
            @RequestParam Integer page,
            @RequestParam Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive){
        Page<Admin> pageAdmin = adminUserService.searchByPage(page, size, keyword, isActive);
        return ResponseEntity.ok(new AdminPaginationDTO(
                pageAdmin.getContent(),
                pageAdmin.getTotalPages(),
                pageAdmin.getTotalElements()
        ));
    }

    @PostMapping(path = "/adminuser/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Admin> createAdminUser(@Valid @RequestBody final Admin admin) throws SQLException {
        if (Objects.isNull(admin)){
            throw new IllegalArgumentException("Object is null");
        }
        Admin savedAdmin = adminUserService.save(admin);
        savedAdmin.setPassword("hidden-for-security");
        return new ResponseEntity<>(savedAdmin, HttpStatus.CREATED);
    }

    @PutMapping(path = "/adminuser/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Admin> updateAdmin(@Valid @RequestBody final Admin admin) throws SQLException {
        if (Objects.isNull(admin)){
            throw new IllegalArgumentException("Object is null");
        }
        Admin savedAdmin = null;
        if (adminUserService.isExisting(admin.getId())){
            savedAdmin = adminUserService.save(admin);
            savedAdmin.setPassword("hidden-for-security");
        }
        return ResponseEntity.ok(savedAdmin);
    }

    @DeleteMapping(path = "/adminuser/deactivate/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> deactivateAdminUser(@PathVariable final String id) throws UserNotFoundException, SQLException {
        if (Objects.isNull(id)){
            throw new IllegalArgumentException("ID is null");
        }
        adminUserService.deactivate(id);

        return ResponseEntity.ok(new MessageResponse("Admin user with ID: " + id + " is deactivated"));
    }

    @PostMapping(path = "/adminuser/activate/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> activateAdminUser(@PathVariable final String id) throws UserNotFoundException, SQLException {
        if (Objects.isNull(id)){
            throw new IllegalArgumentException("ID is null");
        }
        adminUserService.activate(id);

        return ResponseEntity.ok(new MessageResponse("Admin user with ID: " + id + " is activated"));
    }

    @GetMapping(path = "/token/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith("Bearer ")){
            try{
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256(configProperties.getConfigValue("secret-key").getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username = decodedJWT.getSubject();

                Optional<Admin> admin = adminUserService.findByUsername(username);
                if(admin.isPresent()){
                    String accessToken = JWT.create()
                            .withSubject(admin.get().getUsername())
                            .withExpiresAt(new Date(System.currentTimeMillis()
                                    + Long.parseLong(configProperties.getConfigValue("access-token.expire-time"))))
                            .withIssuer(request.getRequestURL().toString())
                            .withClaim("role", List.of(admin.get().getRole().name()))
                            .sign(algorithm);
                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("access-token", accessToken);

                    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
                } else
                    throw new UserNotFoundException("Admin user not found with username: " + username);

            } catch (Exception e) {
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setErrorMessage(e.getMessage());
                errorResponse.setErrorCode(HttpStatus.FORBIDDEN.value());
                new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
            }
        } else {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorMessage("Refresh token is not valid");
            errorResponse.setErrorCode(HttpStatus.FORBIDDEN.value());
            new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
        }
    }



}
