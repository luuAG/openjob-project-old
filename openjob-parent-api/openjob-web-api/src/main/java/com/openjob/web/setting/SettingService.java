package com.openjob.web.setting;

import com.openjob.common.enums.MailCase;
import com.openjob.common.model.Setting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SettingService {
    private final SettingRepository settingRepo;

    public Setting getByMailCase(MailCase mailCase){
        return settingRepo.findByName(mailCase.toString());
    }

    public Setting save(Setting setting){
        return settingRepo.save(setting);
    }
}
