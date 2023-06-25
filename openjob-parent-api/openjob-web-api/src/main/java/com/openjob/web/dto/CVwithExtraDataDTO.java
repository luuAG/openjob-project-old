package com.openjob.web.dto;

import com.openjob.common.model.CV;
import lombok.Setter;

@Setter
public class CVwithExtraDataDTO {
    private CV cv;
    private boolean isChargedToView;
}
