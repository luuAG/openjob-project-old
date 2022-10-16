package com.openjob.web.dto;

import lombok.Getter;

@Getter
public class AuthResponseDTO {
    private String accessToken;
    private final String tokenType = "Bearer";

    public AuthResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }
}
