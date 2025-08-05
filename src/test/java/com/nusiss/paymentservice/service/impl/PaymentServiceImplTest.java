package com.nusiss.paymentservice.service.impl;

import com.nusiss.paymentservice.config.ApiResponse;
import com.nusiss.paymentservice.dto.PaymentRequest;
import com.nusiss.paymentservice.dto.PaymentResult;
import com.nusiss.paymentservice.entity.MoneyAccount;
import com.nusiss.paymentservice.entity.Payment;
import com.nusiss.paymentservice.repository.MoneyAccountRepository;
import com.nusiss.paymentservice.repository.PaymentRepository;
import com.nusiss.paymentservice.service.PaymentProcessor;
import com.nusiss.paymentservice.service.PaymentProcessorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentProcessorFactory paymentProcessorFactory;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MoneyAccountRepository moneyAccountRepository;

    @Mock
    private PaymentProcessor paymentProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 1. 测试支付成功的场景
    @Test
    void testProcessPayment_Success() {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(1L);
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("SGD");
        request.setMethod("WeChat");
        request.setSellerId(10L);

        when(paymentProcessorFactory.createProcessor("WeChat")).thenReturn(paymentProcessor);
        when(paymentProcessor.processPayment(request)).thenReturn(new PaymentResult(true, "Success"));

        Payment savedPayment = new Payment();
        savedPayment.setId(1L);
        when(paymentRepository.save(any())).thenReturn(savedPayment);

        MoneyAccount sellerAccount = new MoneyAccount();
        sellerAccount.setUserId(10L);
        sellerAccount.setBalance(new BigDecimal("500.00"));
        when(moneyAccountRepository.findByUserId(10L)).thenReturn(Optional.of(sellerAccount));
        when(moneyAccountRepository.save(any())).thenReturn(sellerAccount);

        ApiResponse<Payment> response = paymentService.processPayment(request);
        assertTrue(response.isSuccess());
        assertEquals(1L, response.getData().getId());
    }

    // 2. 测试支付失败的场景
    @Test
    void testProcessPayment_Failed() {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(1L);
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("SGD");
        request.setMethod("PayNow");
        request.setSellerId(10L);

        when(paymentProcessorFactory.createProcessor("PayNow")).thenReturn(paymentProcessor);
        when(paymentProcessor.processPayment(request)).thenReturn(new PaymentResult(false, "Insufficient balance"));

        ApiResponse<Payment> response = paymentService.processPayment(request);
        assertFalse(response.isSuccess());
        assertEquals("Insufficient balance", response.getMessage());
    }

    // 3. 测试非法支付方式
    @Test
    void testProcessPayment_UnsupportedMethod() {
        PaymentRequest request = new PaymentRequest();
        request.setMethod("UnsupportedMethod");

        when(paymentProcessorFactory.createProcessor("UnsupportedMethod"))
                .thenThrow(new IllegalArgumentException("Unsupported"));

        ApiResponse<Payment> response = paymentService.processPayment(request);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Unsupported"));
    }

    // 4. 商家账户不存在
    @Test
    void testProcessPayment_SellerAccountNotFound() {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(1L);
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("SGD");
        request.setMethod("WeChat");
        request.setSellerId(10L);

        when(paymentProcessorFactory.createProcessor("WeChat")).thenReturn(paymentProcessor);
        when(paymentProcessor.processPayment(request)).thenReturn(new PaymentResult(true, "Success"));
        when(paymentRepository.save(any())).thenReturn(new Payment());
        when(moneyAccountRepository.findByUserId(10L)).thenReturn(Optional.empty());

        ApiResponse<Payment> response = paymentService.processPayment(request);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("商家账户不存在"));
    }

    // 5. 商家入账异常
    @Test
    void testProcessPayment_SellerAccountSaveError() {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(1L);
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("SGD");
        request.setMethod("WeChat");
        request.setSellerId(10L);

        when(paymentProcessorFactory.createProcessor("WeChat")).thenReturn(paymentProcessor);
        when(paymentProcessor.processPayment(request)).thenReturn(new PaymentResult(true, "Success"));
        when(paymentRepository.save(any())).thenReturn(new Payment());

        MoneyAccount sellerAccount = new MoneyAccount();
        sellerAccount.setUserId(10L);
        sellerAccount.setBalance(new BigDecimal("500.00"));
        when(moneyAccountRepository.findByUserId(10L)).thenReturn(Optional.of(sellerAccount));
        when(moneyAccountRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        ApiResponse<Payment> response = paymentService.processPayment(request);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Payment processing failed"));
    }
}
