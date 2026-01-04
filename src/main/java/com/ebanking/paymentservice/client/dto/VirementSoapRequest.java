package com.ebanking.paymentservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VirementSoapRequest {
    private String ribSource;
    private String ribDestination;
    private Double amount;
    private String motif;
}
