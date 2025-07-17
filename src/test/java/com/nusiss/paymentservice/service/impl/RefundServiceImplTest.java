package com.nusiss.paymentservice.service.impl;

import com.nusiss.paymentservice.entity.MoneyAccount;
import com.nusiss.paymentservice.entity.Payment;
import com.nusiss.paymentservice.entity.Refund;
import com.nusiss.paymentservice.repository.MoneyAccountRepository;
import com.nusiss.paymentservice.repository.PaymentRepository;
import com.nusiss.paymentservice.repository.RefundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefundServiceImplTest {

    @InjectMocks
    private RefundServiceImpl refundService;

    @Mock
    private RefundRepository refundRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MoneyAccountRepository moneyAccountRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 0. 已存在退款记录
    @Test
    void testCreateRefund_alreadyExists() {
        Refund refund = new Refund();
        refund.setPaymentId(1L);

        when(refundRepository.findByPaymentId(1L)).thenReturn(Optional.of(new Refund()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            refundService.createRefund(refund);
        });

        assertEquals("Refund already exists for this payment", ex.getMessage());
        verify(refundRepository, never()).save(any());
    }

    // 1. paymentId 不存在
    @Test
    void testCreateRefund_paymentNotFound() {
        Refund refund = new Refund();
        refund.setPaymentId(2L);

        when(refundRepository.findByPaymentId(2L)).thenReturn(Optional.empty());
        when(paymentRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            refundService.createRefund(refund);
        });

        assertEquals("Payment record not found", ex.getMessage());
        verify(refundRepository, never()).save(any());
    }

    // 2. senderAccount 不存在
    @Test
    void testCreateRefund_senderAccountNotFound() {
        Refund refund = new Refund();
        refund.setPaymentId(3L);

        Payment payment = new Payment();
        payment.setSenderAccountId(99L);
        payment.setAmount(new BigDecimal("88.88"));

        when(refundRepository.findByPaymentId(3L)).thenReturn(Optional.empty());
        when(paymentRepository.findById(3L)).thenReturn(Optional.of(payment));
        when(moneyAccountRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            refundService.createRefund(refund);
        });

        assertEquals("Sender account not found", ex.getMessage());
        verify(refundRepository, never()).save(any());
    }

    // 3. 正常退款成功
    @Test
    void testCreateRefund_success() {
        Refund refund = new Refund();
        refund.setPaymentId(4L);

        Payment payment = new Payment();
        payment.setSenderAccountId(10L);
        payment.setAmount(new BigDecimal("100.00"));

        MoneyAccount account = new MoneyAccount();
        account.setId(10L);
        account.setBalance(new BigDecimal("200.00"));

        when(refundRepository.findByPaymentId(4L)).thenReturn(Optional.empty());
        when(paymentRepository.findById(4L)).thenReturn(Optional.of(payment));
        when(moneyAccountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(refundRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Refund savedRefund = refundService.createRefund(refund);

        assertEquals(new BigDecimal("100.00"), savedRefund.getRefundAmount());
        assertEquals("REFUNDED", savedRefund.getRefundStatus());
        assertEquals("system", savedRefund.getCreateUser());
        assertNotNull(savedRefund.getCreateDatetime());
        verify(moneyAccountRepository).save(account);
    }

    // 4. 查询 getRefundByPaymentId 成功
    @Test
    void testGetRefundByPaymentId_found() {
        Refund refund = new Refund();
        refund.setPaymentId(5L);

        when(refundRepository.findByPaymentId(5L)).thenReturn(Optional.of(refund));

        Refund result = refundService.getRefundByPaymentId(5L);
        assertNotNull(result);
    }

    // 5. 查询 getRefundByPaymentId 失败
    @Test
    void testGetRefundByPaymentId_notFound() {
        when(refundRepository.findByPaymentId(6L)).thenReturn(Optional.empty());

        Refund result = refundService.getRefundByPaymentId(6L);
        assertNull(result);
    }

    // 6. 查询 getAllRefunds
    @Test
    void testGetAllRefunds() {
        List<Refund> list = Arrays.asList(new Refund(), new Refund());
        when(refundRepository.findAll()).thenReturn(list);

        List<Refund> result = refundService.getAllRefunds();
        assertEquals(2, result.size());
    }
}
