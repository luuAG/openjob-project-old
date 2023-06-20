package com.openjob.web.dto;

import com.openjob.common.enums.CvStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCvDto {
    private String userId;
    private String cvId;
    private String firstName;
    private String lastName;
    private String phone;
    private String gender;
    private String email;
    private CvStatus cvStatus;
    private Double point;

    public UserCvDto(String userid, String cvId, String firstName, String lastName, String email,
                     String phone, String gender, Double point, CvStatus status) {
        this.userId = userid;
        this.cvId = cvId;
        this.firstName =firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.point = point;
        this.cvStatus = status;
    }
}
