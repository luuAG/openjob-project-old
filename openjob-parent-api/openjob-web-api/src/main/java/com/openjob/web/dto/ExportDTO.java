package com.openjob.web.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class ExportDTO {
    @NotNull
    private String jobId;

    private List<ExportCvDTO> appliedCVs;

    private List<ExportCvDTO> matchedCVs;
}
