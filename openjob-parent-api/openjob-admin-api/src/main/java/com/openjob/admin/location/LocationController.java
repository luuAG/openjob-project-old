package com.openjob.admin.location;

import com.openjob.admin.district.DistrictService;
import com.openjob.admin.province.ProvinceService;
import com.openjob.common.model.District;
import com.openjob.common.model.Province;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {
    private final ProvinceService provinceService;
    private final DistrictService districtService;

    @GetMapping(path = "/search-province", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Province>> searchProvince(@RequestParam(value = "keyword", required = false) String keyword) {
        return ResponseEntity.ok(provinceService.getByName(keyword));
    }
    @GetMapping(path = "/search-district", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<District>> searchDistrict(@RequestParam("province") String province,
                                                         @RequestParam(value = "keyword", required = false) String keyword) {
        return ResponseEntity.ok(districtService.searchByProvinceAndName(province, keyword));
    }
}
