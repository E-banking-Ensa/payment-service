package com.ebanking.paymentservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VirementRequest {
    
    @NotBlank(message = "RIB source est obligatoire")
    private String ribSource;
    
    @NotBlank(message = "RIB destination est obligatoire")
    private String ribDestination;
    
    @NotNull(message = "Montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private Double amount;
    
    private String motif;
}
