package com.openjob.admin.setting;

import com.openjob.common.model.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Service
public class SettingService {
    @Autowired
    private SettingRepository settingRepo;

    public Optional<Setting> get(String id) {
        return settingRepo.findById(id);
    }

    public String getValue(String name){
        return settingRepo.getValue(name);
    }

    public Collection<Setting> getAll(){
        return settingRepo.findAll();
    }

    public Setting save(Setting object) throws SQLException {
        return settingRepo.save(object);
    }

    public void delete(String id){
        settingRepo.delete(settingRepo.getById(id));
    }

    public Optional<Setting> getByName(String name) {
        return settingRepo.findByName(name);
    }

    public boolean isExistByName(String name) {
        return settingRepo.findByName(name).isPresent();
    }
}
