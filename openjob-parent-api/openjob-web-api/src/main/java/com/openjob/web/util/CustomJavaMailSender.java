package com.openjob.web.util;

import com.openjob.common.enums.MailTemplateVariable;
import com.openjob.common.model.Company;
import com.openjob.common.model.Job;
import com.openjob.common.model.MailSetting;
import com.openjob.common.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

@Component
@EnableAsync(proxyTargetClass = true)
public class CustomJavaMailSender extends JavaMailSenderImpl {

    @Value("${spring.mail.username}")
    private String mailUsername;
    @Value("${spring.mail.password}")
    private String mailPassword;

    private final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    public void getInstance(){

        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.allow8bitmime", "true");
        props.put("mail.smtps.allow8bitmime", "true");
    }

    private void reloadProperties(){
        getInstance();
    }

    @Async
    public void sendMail(MailSetting mailSetting){
        MimeMessagePreparator message = mimeMessage -> {
            MimeMessageHelper message1 = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message1.setFrom(mailUsername);
            message1.setTo(mailSetting.getRecipient());
            message1.setSubject(mailSetting.getSubject());
            message1.setText(processTemplateVariables(mailSetting.getBody(), mailSetting), true);
        };
        try {
            reloadProperties();
            mailSender.send(message);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }

    private String processTemplateVariables(String body, MailSetting mailSetting) {
        if (mailSetting.getCompany() != null) {
            Company company = mailSetting.getCompany();
            body = body.replace(MailTemplateVariable.COMPANY_NAME.getTemplateVariable(), company.getName());
            body = body.replace(MailTemplateVariable.COMPANY_EMAIL.getTemplateVariable(), company.getEmail());
        }
        if (mailSetting.getJob() != null) {
            Job job = mailSetting.getJob();
            body = body.replace(MailTemplateVariable.JOB_TITLE.getTemplateVariable(), job.getTitle());
        }
        if (mailSetting.getUser() != null) {
            User user = mailSetting.getUser();
            body = body.replace(MailTemplateVariable.USER_NAME.getTemplateVariable(), user.getFirstName() +" "+ user.getLastName());
        }
        if (mailSetting.getExtraData() != null) {
            for (Map.Entry<String, String> data : mailSetting.getExtraData().entrySet()) {
                body = body.replace(data.getKey(), data.getValue());
            }
        }

        return body;
    }

}
