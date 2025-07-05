package com.nusiss.paymentservice.service.impl;

import com.nusiss.paymentservice.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 默认支付处理器工厂
 根据支付方式返回对应的 PaymentProcessor 实现类
 */
@Component
public class DefaultPaymentProcessorFactory implements PaymentProcessorFactory {

    @Autowired
    private WeChatPaymentProcessor weChatPaymentProcessor;

    @Autowired
    private PayNowPaymentProcessor payNowPaymentProcessor;

    @Autowired
    private PayLahPaymentProcessor payLahPaymentProcessor;

    @Autowired
    private FaceRecognitionProcessor faceRecognitionProcessor;

    @Override
    public PaymentProcessor createProcessor(String method) {
        switch (method) {
            case "WeChat":
                return weChatPaymentProcessor;
            case "PayNow":
                return payNowPaymentProcessor;
            case "PayLah":
                return payLahPaymentProcessor;
            case "FaceRecognition":
                return faceRecognitionProcessor;
            default:
                throw new IllegalArgumentException("Unsupported payment method: " + method);
        }
    }
}
