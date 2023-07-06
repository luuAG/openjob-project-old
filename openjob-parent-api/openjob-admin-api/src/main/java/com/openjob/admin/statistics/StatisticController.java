package com.openjob.admin.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/statistics")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping(path = "/job")
    public ResponseEntity<JobStatisticDTO> getAmountOfJobForEachYear(){
        return ResponseEntity.ok(statisticService.getJobStatistic());
    }

    @GetMapping(path = "/cv-company")
    public ResponseEntity<List<CvCompanyDTO>> getAmountOfCvApply(@RequestParam(value = "companyId", required = false) String companyId){
        return ResponseEntity.ok(statisticService.getCvStatistic(companyId));
    }

    @GetMapping(path = "/user")
    public ResponseEntity<UserStatisticDTO> getUserStatistic(){
        return ResponseEntity.ok(statisticService.getUserStatistic());
    }

    @GetMapping(path = "/income")
    public ResponseEntity<Map<String, Double>> getIncomeStatistic(){
        return ResponseEntity.ok(statisticService.getIncomeStatistic());
    }


}
