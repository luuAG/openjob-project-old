package com.openjob.web.dto;

import com.openjob.common.enums.CvStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCvDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String phone;
    private String gender;
    private String email;
    private CvStatus cvStatus;
    private Integer point;

    public UserCvDto(String id, String firstName, String lastName, String email,
                     String phone, String gender, Integer point, CvStatus status) {
        this.userId = id;
        this.firstName =firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.point = point;
        this.cvStatus = status;
    }
}
