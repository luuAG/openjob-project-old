package com.openjob.admin.config.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjob.admin.adminuser.AdminUserService;
import com.openjob.admin.config.ConfigProperty;
import com.openjob.common.model.Admin;
import com.openjob.common.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final ConfigProperty configProperties;
    private final AdminUserService adminUserService;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, ConfigProperty configProperties, AdminUserService adminUserService) {
        this.authenticationManager = authenticationManager;
        this.configProperties = configProperties;
        this.adminUserService = adminUserService;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256(configProperties.getConfigValue("secret-key").getBytes());
        String accessToken = JWT.create()
                .withSubject(userDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()
                        + Long.parseLong(configProperties.getConfigValue("access-token.expire-time"))))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("role", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
        String refreshToken = JWT.create()
                .withSubject(userDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()
                        + Long.parseLong(configProperties.getConfigValue("refresh-token.expire-time"))))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        Optional<Admin> admin = adminUserService.findByUsername(userDetails.getUsername());
        Map<String, String> responseDTO = new HashMap<>();
        responseDTO.put("access-token", accessToken);
        responseDTO.put("refresh-token", refreshToken);
        responseDTO.put("id", admin.get().getId());
        responseDTO.put("role", admin.get().getRole().name());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), responseDTO);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setErrorMessage("Bad credentials");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);

    }
}
