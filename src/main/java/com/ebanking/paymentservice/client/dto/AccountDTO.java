package com.ebanking.paymentservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private Long accountId;
    private String rib;
    private Double balance;
    private String accountStatus;
    private String accountType;
}
