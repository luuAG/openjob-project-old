package com.openjob.web.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/statistics")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping(path = "/job")
    public ResponseEntity<JobStatisticDTO> getAmountOfJobForEachYear(){
        return ResponseEntity.ok(statisticService.getJobStatistic());
    }
}
