package com.openjob.admin.hr;

import com.openjob.admin.config.ConfigProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HrController {
    private final ConfigProperty configProperties;




}
