package com.openjob.admin.setting;

import com.openjob.common.model.Setting;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SettingRepositoryTest {
    @Autowired
    private SettingRepository settingRepo;

    @Test
    public void testInsertSetting(){
        Setting s = new Setting();
        s.setName("MAIL_PASSWORD");
        s.setValue("mailcanhan");
        Setting saved = settingRepo.save(s);
        Assert.notNull(saved, "ALO");
    }
}
