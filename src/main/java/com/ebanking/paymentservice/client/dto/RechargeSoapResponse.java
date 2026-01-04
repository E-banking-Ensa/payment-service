package com.ebanking.paymentservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeSoapResponse {
    private boolean success;
    private String message;
    private String rechargeId;
}
