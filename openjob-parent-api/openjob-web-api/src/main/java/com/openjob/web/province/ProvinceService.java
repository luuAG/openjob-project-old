package com.openjob.web.province;

import com.openjob.common.model.Province;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProvinceService {
    private final ProvinceRepository provinceRepo;

    public List<Province> getByName(String name){
        if (Objects.isNull(name))
            name = "";
        return provinceRepo.findByName(name);
    }

}
