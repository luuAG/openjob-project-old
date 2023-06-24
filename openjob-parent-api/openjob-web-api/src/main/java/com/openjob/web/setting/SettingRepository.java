package com.openjob.web.setting;

import com.openjob.common.model.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {
    Setting findByName(String mailCase);
}
