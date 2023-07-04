package com.openjob.web.statistics;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JobStatisticDTO {
    private List<Integer> amountOfJobs;
}
