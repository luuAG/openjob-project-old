package com.openjob.admin.mail;

import com.openjob.admin.setting.SettingService;
import com.openjob.admin.util.CustomJavaMailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final CustomJavaMailSender mailSender;
    private final SettingService settingService;

}
