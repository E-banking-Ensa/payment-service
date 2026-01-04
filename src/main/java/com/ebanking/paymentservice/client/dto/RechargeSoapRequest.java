package com.ebanking.paymentservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeSoapRequest {
    private String rib;
    private String phoneNumber;
    private Double amount;
}
