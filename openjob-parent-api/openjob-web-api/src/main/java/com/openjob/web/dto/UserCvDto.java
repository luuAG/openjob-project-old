package com.openjob.web.dto;

import com.openjob.common.enums.CvStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCvDto {
    private String firstName;
    private String lastName;
    private String phone;
    private String gender;
    private String email;
    private CvStatus cvStatus;
}
