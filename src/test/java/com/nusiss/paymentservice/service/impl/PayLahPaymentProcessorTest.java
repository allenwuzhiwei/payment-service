package com.nusiss.paymentservice.service.impl;

import com.nusiss.paymentservice.dto.PaymentRequest;
import com.nusiss.paymentservice.dto.PaymentResult;
import com.nusiss.paymentservice.entity.MoneyAccount;
import com.nusiss.paymentservice.repository.MoneyAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PayLahPaymentProcessorTest {

    @InjectMocks
    private PayLahPaymentProcessor processor;

    @Mock
    private MoneyAccountRepository moneyAccountRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 1. 用户账户不存在
    @Test
    void testAccountNotFound() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(1L);

        when(moneyAccountRepository.findByUserIdAndAccountType(1L, "PayLah")).thenReturn(null);

        PaymentResult result = processor.processPayment(request);

        assertFalse(result.isSuccess());
        assertEquals("PayLah account not found", result.getMessage());
        verify(moneyAccountRepository, never()).save(any());
    }

    // 2. 用户余额不足
    @Test
    void testInsufficientBalance() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(2L);
        request.setAmount(new BigDecimal("100.00"));

        MoneyAccount account = new MoneyAccount();
        account.setBalance(new BigDecimal("50.00"));

        when(moneyAccountRepository.findByUserIdAndAccountType(2L, "PayLah")).thenReturn(account);

        PaymentResult result = processor.processPayment(request);

        assertFalse(result.isSuccess());
        assertEquals("Insufficient PayLah balance", result.getMessage());
        verify(moneyAccountRepository, never()).save(any());
    }

    // 3. 支付成功：扣款 + 保存账户
    @Test
    void testSuccessfulPayment() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(3L);
        request.setAmount(new BigDecimal("30.00"));

        MoneyAccount account = new MoneyAccount();
        account.setBalance(new BigDecimal("100.00"));

        when(moneyAccountRepository.findByUserIdAndAccountType(3L, "PayLah")).thenReturn(account);

        PaymentResult result = processor.processPayment(request);

        assertTrue(result.isSuccess());
        assertEquals("PayLah payment successful", result.getMessage());
        verify(moneyAccountRepository).save(account);

        // 验证余额扣除后是否正确
        assertEquals(new BigDecimal("70.00"), account.getBalance());
    }
}
