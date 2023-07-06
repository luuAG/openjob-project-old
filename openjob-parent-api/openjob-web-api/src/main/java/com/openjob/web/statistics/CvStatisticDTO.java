package com.openjob.web.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class CvStatisticDTO {
    private String jobTitle;
    private Date jobCreateAt;
    private Long appliedCv;
    private Long acceptedCv;
    private Long rejectedCv;

}
