package com.openjob.web.dto;

import com.openjob.common.model.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCVwithExtraDataDTO {
    private User user;
    private boolean isChargedToView;
}
