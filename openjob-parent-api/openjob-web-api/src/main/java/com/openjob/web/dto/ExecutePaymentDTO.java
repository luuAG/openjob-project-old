package com.openjob.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecutePaymentDTO {
    private String paymentId;
    private String payerId;
    private String companyId;
}
