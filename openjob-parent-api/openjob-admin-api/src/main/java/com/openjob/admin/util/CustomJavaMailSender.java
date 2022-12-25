package com.openjob.admin.util;

import com.openjob.admin.setting.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class CustomJavaMailSender extends JavaMailSenderImpl {
    @Autowired
    private SettingService settingService;

    private JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    public void getInstance(){

        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(settingService.getValue("MAIL_USERNAME"));
        mailSender.setPassword(settingService.getValue("MAIL_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.allow8bitmime", "true");
        props.put("mail.smtps.allow8bitmime", "true");
    }

    public void reloadProperties(){
        getInstance();
    }

    public JavaMailSenderImpl getMailSender(){
        return this.mailSender;
    }
}
