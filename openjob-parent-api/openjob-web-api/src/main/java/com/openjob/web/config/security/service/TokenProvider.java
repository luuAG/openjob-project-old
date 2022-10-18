package com.openjob.web.config.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.openjob.web.config.properties.AppProperties;
import com.openjob.web.config.security.info.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;

@Service
public class TokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    private AppProperties appProperties;

    public TokenProvider(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String createAccessToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec());

        Algorithm algorithm = Algorithm.HMAC256(appProperties.getAuth().getTokenSecret().getBytes());
        return JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withExpiresAt(expiryDate)
                .withClaim("role", userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

    }

    public String createRefreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec());

        Algorithm algorithm = Algorithm.HMAC256(appProperties.getAuth().getTokenSecret().getBytes());
        return  JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withExpiresAt(expiryDate)
                .sign(algorithm);

    }

//    public String getUserIdFromToken(String token) {
//        Claims claims = Jwts.parser()
//                .setSigningKey(appProperties.getAuth().getTokenSecret())
//                .parseClaimsJws(token)
//                .getBody();
//
//        return claims.getSubject();
//    }

    public String getEmailFromToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(appProperties.getAuth().getTokenSecret().getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getSubject();
    }



    public boolean validateToken(String authToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(appProperties.getAuth().getTokenSecret().getBytes()); //use more secure key
            JWTVerifier verifier = JWT.require(algorithm)
                    .build(); //Reusable verifier instance
            verifier.verify(authToken);
            return true;
        } catch (JWTVerificationException exception){
            logger.error(exception.getMessage());
        }
        return false;
    }
}
