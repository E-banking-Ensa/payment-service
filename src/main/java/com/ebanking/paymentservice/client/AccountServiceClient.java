package com.ebanking.paymentservice.client;

import com.ebanking.paymentservice.client.dto.AccountDTO;
import com.ebanking.paymentservice.client.dto.BalanceUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "accountservice", path = "/api/accounts")
public interface AccountServiceClient {
    
    @GetMapping("/rib/{rib}")
    AccountDTO getAccountByRib(@PathVariable("rib") String rib);

    @PostMapping("/balance/update")
    void updateBalance(@RequestBody BalanceUpdateRequest request);
}
