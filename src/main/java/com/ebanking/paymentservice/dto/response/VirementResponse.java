package com.ebanking.paymentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VirementResponse {
    private Long paymentId;
    private String status;
    private String message;
    private Double amount;
    private String ribSource;
    private String ribDestination;
    private LocalDateTime timestamp;
}
