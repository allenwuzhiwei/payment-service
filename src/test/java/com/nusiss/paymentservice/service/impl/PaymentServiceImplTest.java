package com.nusiss.paymentservice.service.impl;

import com.nusiss.paymentservice.config.ApiResponse;
import com.nusiss.paymentservice.dto.PaymentRequest;
import com.nusiss.paymentservice.entity.MoneyAccount;
import com.nusiss.paymentservice.entity.Payment;
import com.nusiss.paymentservice.repository.MoneyAccountRepository;
import com.nusiss.paymentservice.repository.PaymentRepository;
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
    private MoneyAccountRepository moneyAccountRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessPayment_AccountNotFound() {
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(123L);
        request.setUserId(1L);
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("CNY");
        request.setMethod("WeChat");

        when(moneyAccountRepository.findByUserIdAndCurrency(1L, "CNY"))
                .thenReturn(Optional.empty());

        ApiResponse<Payment> response = paymentService.processPayment(request);
        assertFalse(response.isSuccess());
        assertEquals("未找到对应的付款账户", response.getMessage());
    }

    @Test
    void testProcessPayment_InsufficientBalance() {
        MoneyAccount account = new MoneyAccount();
        account.setId(10L); // 必加
        account.setBalance(new BigDecimal("50.00"));

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(123L);
        request.setUserId(1L);
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("CNY");
        request.setMethod("WeChat");

        when(moneyAccountRepository.findByUserIdAndCurrency(1L, "CNY"))
                .thenReturn(Optional.of(account));

        ApiResponse<Payment> response = paymentService.processPayment(request);
        assertFalse(response.isSuccess());
        assertEquals("账户余额不足，支付失败", response.getMessage());
    }

    @Test
    void testProcessPayment_Success() {
        MoneyAccount account = new MoneyAccount();
        account.setId(10L);
        account.setBalance(new BigDecimal("200.00"));

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(123L);
        request.setUserId(1L);
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("CNY");
        request.setMethod("WeChat");

        when(moneyAccountRepository.findByUserIdAndCurrency(1L, "CNY"))
                .thenReturn(Optional.of(account));
        when(moneyAccountRepository.save(any())).thenReturn(account);

        Payment payment = new Payment();
        payment.setOrderId(123L);
        payment.setSenderAccountId(10L);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCurrency("CNY"); // 注意要和 request 一致
        payment.setPaymentStatus("PAID");

        when(paymentRepository.save(any())).thenReturn(payment);

        ApiResponse<Payment> response = paymentService.processPayment(request);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals("PAID", response.getData().getPaymentStatus());
        assertEquals(new BigDecimal("100.00"), response.getData().getAmount());
    }
}
