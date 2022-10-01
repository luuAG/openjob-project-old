package com.openjob.admin.config.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjob.admin.config.ConfigProperty;
import com.openjob.common.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private final ConfigProperty configProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/login")
                || request.getServletPath().equals("/token/refresh")){
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith("Bearer ")){
                String username = null;
                String role = null;
                try{
                   String token = authorizationHeader.substring("Bearer ".length());
                   Algorithm algorithm = Algorithm.HMAC256(configProperties.getConfigValue("secret-key").getBytes());
                   JWTVerifier verifier = JWT.require(algorithm).build();
                   DecodedJWT decodedJWT = verifier.verify(token);
                   username = decodedJWT.getSubject();
                   role = decodedJWT.getClaim("role").asArray(String.class)[0];
                   Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                   authorities.add(new SimpleGrantedAuthority(role));

                   UsernamePasswordAuthenticationToken authenticationToken =
                           new UsernamePasswordAuthenticationToken(username, null, authorities);
                   SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                   filterChain.doFilter(request, response);
               } catch (Exception e) {
                   response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                   response.setStatus(HttpStatus.FORBIDDEN.value());
                   ErrorResponse errorResponse = new ErrorResponse();
                   errorResponse.setErrorMessage("Username "+username+ " with role " + role + " doesnt have permission");
                   errorResponse.setErrorCode(HttpStatus.FORBIDDEN.value());
                   new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
                   response.getOutputStream().flush();
               }
            }
            else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
