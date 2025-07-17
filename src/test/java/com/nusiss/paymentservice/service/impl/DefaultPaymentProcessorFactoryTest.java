package com.nusiss.paymentservice.service.impl;

import com.nusiss.paymentservice.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultPaymentProcessorFactoryTest {

    private DefaultPaymentProcessorFactory factory;

    private WeChatPaymentProcessor weChatProcessor;
    private PayNowPaymentProcessor payNowProcessor;
    private PayLahPaymentProcessor payLahProcessor;
    private FaceRecognitionProcessor faceProcessor;

    @BeforeEach
    void setUp() throws Exception {
        // 创建 Mock 实例
        weChatProcessor = mock(WeChatPaymentProcessor.class);
        payNowProcessor = mock(PayNowPaymentProcessor.class);
        payLahProcessor = mock(PayLahPaymentProcessor.class);
        faceProcessor = mock(FaceRecognitionProcessor.class);

        // 创建工厂实例
        factory = new DefaultPaymentProcessorFactory();

        // 使用反射注入私有字段
        injectPrivateField(factory, "weChatPaymentProcessor", weChatProcessor);
        injectPrivateField(factory, "payNowPaymentProcessor", payNowProcessor);
        injectPrivateField(factory, "payLahPaymentProcessor", payLahProcessor);
        injectPrivateField(factory, "faceRecognitionProcessor", faceProcessor);
    }

    // 反射工具方法
    private void injectPrivateField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }


    @Test
    void testCreateProcessor_WeChat() {
        PaymentProcessor result = factory.createProcessor("WeChat");
        assertEquals(weChatProcessor, result);
    }

    @Test
    void testCreateProcessor_PayNow() {
        PaymentProcessor result = factory.createProcessor("PayNow");
        assertEquals(payNowProcessor, result);
    }

    @Test
    void testCreateProcessor_PayLah() {
        PaymentProcessor result = factory.createProcessor("PayLah");
        assertEquals(payLahProcessor, result);
    }

    @Test
    void testCreateProcessor_FaceRecognition() {
        PaymentProcessor result = factory.createProcessor("FaceRecognition");
        assertEquals(faceProcessor, result);
    }

    @Test
    void testCreateProcessor_UnsupportedMethod() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                factory.createProcessor("ApplePay"));
        assertEquals("Unsupported payment method: ApplePay", ex.getMessage());
    }
}
