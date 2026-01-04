package com.ebanking.paymentservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VirementSoapResponse {
    private boolean success;
    private String message;
    private String transactionId;
}
