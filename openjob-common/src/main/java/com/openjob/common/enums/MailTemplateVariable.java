package com.openjob.common.enums;

public enum MailTemplateVariable {


    COMPANY_NAME("[[companyName]]"),
    JOB_TITLE("[[jobTitle]]"),
    USER_NAME("[[userName]]"),
    REASON("[[reason]]");

    private final String templateVariable;

    public String getTemplateVariable(){
        return this.templateVariable;
    }

    MailTemplateVariable(String s) {
        templateVariable = s;
    }
}
