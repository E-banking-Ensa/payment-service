package com.ebanking.paymentservice.repository;

import com.ebanking.paymentservice.entity.Payment;
import com.ebanking.paymentservice.entity.PaymentStatus;
import com.ebanking.paymentservice.entity.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByRibSource(String ribSource);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByPaymentType(PaymentType paymentType);
    
    List<Payment> findByRibSourceAndCreatedAtBetween(
            String ribSource, 
            LocalDateTime start, 
            LocalDateTime end
    );
}
