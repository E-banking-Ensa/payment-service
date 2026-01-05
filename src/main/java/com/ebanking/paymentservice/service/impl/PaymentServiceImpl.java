package com.ebanking.paymentservice.service.impl;

import com.ebanking.paymentservice.client.AccountServiceClient;
import com.ebanking.paymentservice.client.LegacyAdapterClient;
import com.ebanking.paymentservice.client.dto.*;
import com.ebanking.paymentservice.client.dto.BalanceUpdateRequest;
import com.ebanking.paymentservice.dto.request.MobileRechargeRequest;
import com.ebanking.paymentservice.dto.request.VirementRequest;
import com.ebanking.paymentservice.dto.response.MobileRechargeResponse;
import com.ebanking.paymentservice.dto.response.VirementResponse;
import com.ebanking.paymentservice.entity.Payment;
import com.ebanking.paymentservice.entity.PaymentStatus;
import com.ebanking.paymentservice.entity.PaymentType;
import com.ebanking.paymentservice.exception.AccountNotFoundException;
import com.ebanking.paymentservice.exception.InsufficientBalanceException;
import com.ebanking.paymentservice.exception.PaymentException;
import com.ebanking.paymentservice.repository.PaymentRepository;
import com.ebanking.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    
    private final AccountServiceClient accountServiceClient;
    private final LegacyAdapterClient legacyAdapterClient;
    private final PaymentRepository paymentRepository;
    
    @Override
    public VirementResponse executeVirement(VirementRequest request) {
        log.info("Début du virement - Source: {}, Destination: {}, Montant: {}", 
                 request.getRibSource(), request.getRibDestination(), request.getAmount());
        
        // 1. Créer l'enregistrement de paiement
        Payment payment = new Payment();
        payment.setPaymentType(PaymentType.VIREMENT);
        payment.setRibSource(request.getRibSource());
        payment.setRibDestination(request.getRibDestination());
        payment.setAmount(request.getAmount());
        payment.setDescription(request.getMotif() != null ? request.getMotif() : "Virement bancaire");
        payment = paymentRepository.save(payment);
        
        try {
            // 2. Vérifier le compte source via Account Service
            log.debug("Vérification du compte source: {}", request.getRibSource());
            AccountDTO sourceAccount = accountServiceClient.getAccountByRib(request.getRibSource());
            
            if (sourceAccount == null) {
                throw new AccountNotFoundException("Compte source introuvable: " + request.getRibSource());
            }
            
            if (!"ACTIVE".equalsIgnoreCase(sourceAccount.getAccountStatus())) {
                throw new PaymentException("Compte source inactif: " + sourceAccount.getAccountStatus());
            }
            
            // 3. Vérifier le solde
            log.debug("Solde disponible: {}, Montant requis: {}", 
                     sourceAccount.getBalance(), request.getAmount());
            if (sourceAccount.getBalance() < request.getAmount()) {
                throw new InsufficientBalanceException(
                    String.format("Solde insuffisant. Disponible: %.2f DH, Requis: %.2f DH", 
                                  sourceAccount.getBalance(), request.getAmount())
                );
            }
            
            // 4. Vérifier le compte destination
            log.debug("Vérification du compte destination: {}", request.getRibDestination());
            AccountDTO destAccount = accountServiceClient.getAccountByRib(request.getRibDestination());
            
            if (destAccount == null) {
                throw new AccountNotFoundException("Compte destination introuvable: " + request.getRibDestination());
            }
            
            if (!"ACTIVE".equalsIgnoreCase(destAccount.getAccountStatus())) {
                throw new PaymentException("Compte destination inactif: " + destAccount.getAccountStatus());
            }
            
            // 5. Exécuter le virement via Legacy Adapter (SOAP)
            log.info("Exécution du virement via Legacy Adapter");
            VirementSoapRequest soapRequest = new VirementSoapRequest(
                request.getRibSource(),
                request.getRibDestination(),
                request.getAmount(),
                request.getMotif()
            );
            
            VirementSoapResponse soapResponse = legacyAdapterClient.executeVirement(soapRequest);
            
            if (soapResponse.isSuccess()) {
                // 6. Mettre à jour le statut du paiement
                payment.setStatus(PaymentStatus.SUCCESS);
                payment = paymentRepository.save(payment);
                
                // 6b. Mettre à jour les soldes dans Account Service
                try {
                    log.info("Mise à jour des soldes dans Account Service");
                    // Débit compte source
                    accountServiceClient.updateBalance(new BalanceUpdateRequest(request.getRibSource(), -request.getAmount()));
                    // Crédit compte destination
                    accountServiceClient.updateBalance(new BalanceUpdateRequest(request.getRibDestination(), request.getAmount()));
                } catch (Exception e) {
                    log.error("Erreur lors de la mise à jour des soldes (Transaction réussie mais soldes non mis à jour): {}", e.getMessage());
                    // On ne fail pas la transaction car l'argent a "bougé" côté Legacy/SOAP (simulé)
                    // Dans un vrai système distribué, il faudrait une saga ou un mécanisme de compensation
                }

                log.info("Virement réussi - ID: {}, Transaction: {}", 
                         payment.getId(), soapResponse.getTransactionId());
                
                return new VirementResponse(
                    payment.getId(),
                    "SUCCESS",
                    "Virement effectué avec succès",
                    request.getAmount(),
                    request.getRibSource(),
                    request.getRibDestination(),
                    LocalDateTime.now()
                );
            } else {
                throw new PaymentException("Échec du virement: " + soapResponse.getMessage());
            }
            
        } catch (Exception e) {
            // 7. Gérer l'échec
            log.error("Erreur lors du virement: {}", e.getMessage(), e);
            payment.setStatus(PaymentStatus.FAILED);
            payment.setErrorMessage(e.getMessage());
            paymentRepository.save(payment);
            
            return new VirementResponse(
                payment.getId(),
                "FAILED",
                "Échec du virement: " + e.getMessage(),
                request.getAmount(),
                request.getRibSource(),
                request.getRibDestination(),
                LocalDateTime.now()
            );
        }
    }
    
    @Override
    public MobileRechargeResponse executeMobileRecharge(MobileRechargeRequest request) {
        log.info("Début de la recharge mobile - RIB: {}, Téléphone: {}, Montant: {}", 
                 request.getRib(), request.getPhoneNumber(), request.getAmount());
        
        // 1. Créer l'enregistrement de paiement
        Payment payment = new Payment();
        payment.setPaymentType(PaymentType.MOBILE_RECHARGE);
        payment.setRibSource(request.getRib());
        payment.setPhoneNumber(request.getPhoneNumber());
        payment.setAmount(request.getAmount());
        payment.setDescription("Recharge mobile " + request.getPhoneNumber());
        payment = paymentRepository.save(payment);
        
        try {
            // 2. Vérifier le compte source
            log.debug("Vérification du compte: {}", request.getRib());
            AccountDTO sourceAccount = accountServiceClient.getAccountByRib(request.getRib());
            
            if (sourceAccount == null) {
                throw new AccountNotFoundException("Compte introuvable: " + request.getRib());
            }
            
            if (!"ACTIVE".equalsIgnoreCase(sourceAccount.getAccountStatus())) {
                throw new PaymentException("Compte inactif: " + sourceAccount.getAccountStatus());
            }
            
            // 3. Vérifier le solde
            log.debug("Solde disponible: {}, Montant requis: {}", 
                     sourceAccount.getBalance(), request.getAmount());
            if (sourceAccount.getBalance() < request.getAmount()) {
                throw new InsufficientBalanceException(
                    String.format("Solde insuffisant. Disponible: %.2f DH, Requis: %.2f DH", 
                                  sourceAccount.getBalance(), request.getAmount())
                );
            }
            
            // 4. Exécuter la recharge via Legacy Adapter (SOAP)
            log.info("Exécution de la recharge via Legacy Adapter");
            RechargeSoapRequest soapRequest = new RechargeSoapRequest(
                request.getRib(),
                request.getPhoneNumber(),
                request.getAmount()
            );
            
            RechargeSoapResponse soapResponse = legacyAdapterClient.executeMobileRecharge(soapRequest);
            
            if (soapResponse.isSuccess()) {
                // 5. Mettre à jour le statut
                payment.setStatus(PaymentStatus.SUCCESS);
                payment = paymentRepository.save(payment);
                
                // 5b. Mettre à jour le solde dans Account Service
                try {
                    log.info("Mise à jour du solde pour recharge mobile");
                    accountServiceClient.updateBalance(new BalanceUpdateRequest(request.getRib(), -request.getAmount()));
                } catch (Exception e) {
                    log.error("Erreur lors de la mise à jour du solde (Recharge réussie mais solde non débité): {}", e.getMessage());
                }
                
                log.info("Recharge réussie - ID: {}, Recharge ID: {}", 
                         payment.getId(), soapResponse.getRechargeId());
                
                return new MobileRechargeResponse(
                    payment.getId(),
                    "SUCCESS",
                    "Recharge effectuée avec succès",
                    request.getAmount(),
                    request.getPhoneNumber(),
                    LocalDateTime.now()
                );
            } else {
                throw new PaymentException("Échec de la recharge: " + soapResponse.getMessage());
            }
            
        } catch (Exception e) {
            // 6. Gérer l'échec
            log.error("Erreur lors de la recharge mobile: {}", e.getMessage(), e);
            payment.setStatus(PaymentStatus.FAILED);
            payment.setErrorMessage(e.getMessage());
            paymentRepository.save(payment);
            
            return new MobileRechargeResponse(
                payment.getId(),
                "FAILED",
                "Échec de la recharge: " + e.getMessage(),
                request.getAmount(),
                request.getPhoneNumber(),
                LocalDateTime.now()
            );
        }
    }
}
