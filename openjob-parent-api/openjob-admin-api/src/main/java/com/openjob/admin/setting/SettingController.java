package com.openjob.admin.setting;


import com.openjob.common.model.Setting;
import com.openjob.common.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class SettingController {
    private final SettingService settingService;

    @GetMapping(path = "/settings", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Setting>> getAllSetting() {
        return ResponseEntity.ok(settingService.getAll());
    }

    @GetMapping(path = "/setting/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Setting> getSetting(@PathVariable String name) {
        Optional<Setting> optionalSetting = settingService.getByName(name);
        if (optionalSetting.isPresent()){
            return ResponseEntity.ok(optionalSetting.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping(path = "/setting/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Setting> createASetting(@RequestBody Setting setting) throws SQLException {
        Setting savedSetting = settingService.save(setting);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSetting);
    }

    @PostMapping(path = "/setting/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Setting> updateSetting(@RequestBody Setting setting) throws SQLException {
        Optional<Setting> existingSetting = settingService.get(setting.getId());
        if (existingSetting.isPresent()){
            existingSetting.get().setName(setting.getName());
            existingSetting.get().setValue(setting.getValue());
            existingSetting.get().setExtraValue(setting.getExtraValue());
            return ResponseEntity.status(HttpStatus.OK).body(settingService.save(existingSetting.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @DeleteMapping(path = "/setting/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> deleteSetting(@PathVariable String id) {
        MessageResponse response = new MessageResponse("Setting not found, check id again");
        Optional<Setting> optionalSetting = settingService.get(id);
        if (optionalSetting.isPresent()){
            settingService.delete(id);
            response = new MessageResponse("Setting is deleted");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

    }
}
