package com.ebanking.paymentservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobileRechargeRequest {
    
    @NotBlank(message = "RIB est obligatoire")
    private String rib;
    
    @NotBlank(message = "Numéro de téléphone est obligatoire")
    @Pattern(regexp = "^(\\+212|0)[5-7][0-9]{8}$", 
             message = "Format de téléphone invalide (ex: 0612345678 ou +212612345678)")
    private String phoneNumber;
    
    @NotNull(message = "Montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    @Min(value = 10, message = "Montant minimum : 10 DH")
    @Max(value = 500, message = "Montant maximum : 500 DH")
    private Double amount;
}
