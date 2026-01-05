package com.ebanking.paymentservice.controller;

import com.ebanking.paymentservice.dto.request.MobileRechargeRequest;
import com.ebanking.paymentservice.dto.request.VirementRequest;
import com.ebanking.paymentservice.dto.response.MobileRechargeResponse;
import com.ebanking.paymentservice.dto.response.VirementResponse;
import com.ebanking.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/virement")
    public ResponseEntity<VirementResponse> executeVirement(
            @Valid @RequestBody VirementRequest request) {
        
        log.info("Requête de virement reçue: {}", request);
        VirementResponse response = paymentService.executeVirement(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/recharge")
    public ResponseEntity<MobileRechargeResponse> executeMobileRecharge(
            @Valid @RequestBody MobileRechargeRequest request) {
        
        log.info("Requête de recharge mobile reçue: {}", request);
        MobileRechargeResponse response = paymentService.executeMobileRecharge(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Payment Service is running");
    }
}
