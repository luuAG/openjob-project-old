package com.openjob.admin.setting;

import com.openjob.common.model.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class SettingService {
    @Autowired
    private SettingRepository settingRepo;

    public String getValue(String name){
        return settingRepo.getValue(name);
    }


    public Setting save(Setting object) throws SQLException {
        return settingRepo.save(object);
    }

}
