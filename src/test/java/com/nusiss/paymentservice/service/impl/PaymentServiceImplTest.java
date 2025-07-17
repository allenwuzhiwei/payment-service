package com.nusiss.paymentservice.service.impl;

import com.nusiss.paymentservice.config.ApiResponse;
import com.nusiss.paymentservice.dto.PaymentRequest;
import com.nusiss.paymentservice.dto.PaymentResult;
import com.nusiss.paymentservice.entity.Payment;
import com.nusiss.paymentservice.repository.PaymentRepository;
import com.nusiss.paymentservice.service.PaymentProcessor;
import com.nusiss.paymentservice.service.PaymentProcessorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentProcessorFactory processorFactory;

    @Mock
    private PaymentProcessor processor;

    @Mock
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 测试成功支付流程
    @Test
    void testProcessPayment_success() {
        // 准备请求
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(1L);
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("SGD");
        request.setMethod("PayNow");

        // 模拟成功结果
        PaymentResult result = new PaymentResult(true, "Success");

        // 模拟保存后的 Payment 对象
        Payment savedPayment = new Payment();
        savedPayment.setId(10L); // 模拟数据库生成的主键
        savedPayment.setOrderId(1L);

        // 模拟行为
        when(processorFactory.createProcessor("PayNow")).thenReturn(processor);
        when(processor.processPayment(request)).thenReturn(result);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // 执行方法
        ApiResponse<Payment> response = paymentService.processPayment(request);

        // 断言结果
        assertTrue(response.isSuccess());
        assertEquals(10L, response.getData().getId());
        verify(paymentRepository).save(any(Payment.class));
    }

    // 测试 processor 返回失败
    @Test
    void testProcessPayment_processorFailed() {
        PaymentRequest request = new PaymentRequest();
        request.setMethod("PayLah");

        PaymentResult result = new PaymentResult(false, "Insufficient balance");

        when(processorFactory.createProcessor("PayLah")).thenReturn(processor);
        when(processor.processPayment(request)).thenReturn(result);

        ApiResponse<Payment> response = paymentService.processPayment(request);

        assertFalse(response.isSuccess());
        assertEquals("Insufficient balance", response.getMessage());
        verify(paymentRepository, never()).save(any());
    }

    // 测试不支持的支付方式（IllegalArgumentException）
    @Test
    void testProcessPayment_unsupportedMethod() {
        PaymentRequest request = new PaymentRequest();
        request.setMethod("UnknownMethod");

        when(processorFactory.createProcessor("UnknownMethod")).thenThrow(new IllegalArgumentException("Not supported"));

        ApiResponse<Payment> response = paymentService.processPayment(request);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Unsupported payment method"));
        verify(paymentRepository, never()).save(any());
    }

    // 测试系统级异常（Exception）
    @Test
    void testProcessPayment_generalException() {
        PaymentRequest request = new PaymentRequest();
        request.setMethod("WeChat");

        when(processorFactory.createProcessor("WeChat")).thenReturn(processor);
        when(processor.processPayment(request)).thenThrow(new RuntimeException("DB Error"));

        ApiResponse<Payment> response = paymentService.processPayment(request);

        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("Payment processing failed"));
        verify(paymentRepository, never()).save(any());
    }
}
