package com.openjob.admin.district;

import com.openjob.common.model.District;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DistrictService {
    private final DistrictRepository districtRepo;

    public List<District> getByName(String name){
        return districtRepo.findByName(name);
    }

    public List<District> searchByProvinceAndName(String province, String keyword) {
        if (Objects.isNull(keyword))
            keyword = "";
        return districtRepo.searchByProvinceAndName(province, keyword);
    }
}
