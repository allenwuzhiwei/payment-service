package com.nusiss.paymentservice.service;



/*
 支付处理器工厂接口
 定义创建 PaymentProcessor 的方法
 */
public interface PaymentProcessorFactory {

    /*
     根据支付方式创建对应的 PaymentProcessor 实例
     @param method 支付方式 (WeChat / PayNow / PayLah / FaceRecognition)
     @return PaymentProcessor
     */
    PaymentProcessor createProcessor(String method);
}
