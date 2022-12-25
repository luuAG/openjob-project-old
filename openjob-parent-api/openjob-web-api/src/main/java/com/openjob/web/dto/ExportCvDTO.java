package com.openjob.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExportCvDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String gender;
    private String url;
}
