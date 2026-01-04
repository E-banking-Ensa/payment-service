package com.ebanking.paymentservice.client;

import com.ebanking.paymentservice.client.dto.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "accountservice", path = "/api/accounts")
public interface AccountServiceClient {
    
    @GetMapping("/rib/{rib}")
    AccountDTO getAccountByRib(@PathVariable("rib") String rib);
}
