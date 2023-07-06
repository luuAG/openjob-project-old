package com.openjob.web.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "/statistics")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping(path = "/cv/{companyId}")
    public ResponseEntity<List<CvStatisticDTO>> getCvStatistics(@PathVariable("companyId") String companyId,
            @RequestParam(value = "startDate", required = false) Date startDate,
            @RequestParam(value = "endDate", required = false) Date endDate) {
        return ResponseEntity.ok(statisticService.getCvStatistic(companyId,startDate, endDate));
    }

}
