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

class WeChatPaymentProcessorTest {

    @InjectMocks
    private WeChatPaymentProcessor processor;

    @Mock
    private MoneyAccountRepository moneyAccountRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 1. 账户不存在
    @Test
    void testAccountNotFound() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(1L);

        when(moneyAccountRepository.findByUserIdAndAccountType(1L, "WeChat")).thenReturn(null);

        PaymentResult result = processor.processPayment(request);

        assertFalse(result.isSuccess());
        assertEquals("WeChat account not found", result.getMessage());
        verify(moneyAccountRepository, never()).save(any());
    }

    // 2. 余额不足
    @Test
    void testInsufficientBalance() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(2L);
        request.setAmount(new BigDecimal("200.00"));

        MoneyAccount account = new MoneyAccount();
        account.setBalance(new BigDecimal("100.00"));

        when(moneyAccountRepository.findByUserIdAndAccountType(2L, "WeChat")).thenReturn(account);

        PaymentResult result = processor.processPayment(request);

        assertFalse(result.isSuccess());
        assertEquals("Insufficient WeChat balance", result.getMessage());
        verify(moneyAccountRepository, never()).save(any());
    }

    // 3. 支付成功
    @Test
    void testSuccessfulPayment() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId(3L);
        request.setAmount(new BigDecimal("50.00"));

        MoneyAccount account = new MoneyAccount();
        account.setBalance(new BigDecimal("100.00"));

        when(moneyAccountRepository.findByUserIdAndAccountType(3L, "WeChat")).thenReturn(account);

        PaymentResult result = processor.processPayment(request);

        assertTrue(result.isSuccess());
        assertEquals("WeChat payment successful", result.getMessage());
        assertEquals(new BigDecimal("50.00"), account.getBalance());
        verify(moneyAccountRepository).save(account);
    }
}
