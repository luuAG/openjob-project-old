package com.openjob.admin.setting;

import com.openjob.common.model.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {

    @Query("select s.value from Setting s where s.name=?1")
    String getValue(String name);
}
