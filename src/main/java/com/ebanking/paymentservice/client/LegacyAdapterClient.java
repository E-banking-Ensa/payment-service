package com.ebanking.paymentservice.client;

import com.ebanking.paymentservice.client.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "legacy-adapter-service", path = "/api/v1/legacy")
public interface LegacyAdapterClient {
    
    @PostMapping("/virement")
    VirementSoapResponse executeVirement(@RequestBody VirementSoapRequest request);
    
    @PostMapping("/recharge")
    RechargeSoapResponse executeMobileRecharge(@RequestBody RechargeSoapRequest request);
}
