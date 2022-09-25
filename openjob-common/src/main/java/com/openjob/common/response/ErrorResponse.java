package com.openjob.common.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {
    private String errorMessage;
    private Integer errorCode;
}
