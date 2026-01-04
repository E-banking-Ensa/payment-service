package com.ebanking.paymentservice.service;

import com.ebanking.paymentservice.dto.request.MobileRechargeRequest;
import com.ebanking.paymentservice.dto.request.VirementRequest;
import com.ebanking.paymentservice.dto.response.MobileRechargeResponse;
import com.ebanking.paymentservice.dto.response.VirementResponse;

public interface PaymentService {
    VirementResponse executeVirement(VirementRequest request);
    MobileRechargeResponse executeMobileRecharge(MobileRechargeRequest request);
}
