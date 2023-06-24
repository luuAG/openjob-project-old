package com.openjob.common.model;

import lombok.Getter;

import java.util.Map;

@Getter
public class MailSetting {
    private String recipient;
    private String subject;
    private String body;
    private User user;
    private Company company;
    private Job job;
    private Map<String, String> extraData;

    public MailSetting(String recipient, String subject, String body, User user, Company company, Job job, Map<String, String> extraData) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.user = user;
        this.company = company;
        this.job = job;
        this.extraData = extraData;
    }
}
