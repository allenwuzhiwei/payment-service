package com.nusiss.paymentservice.service;

import com.nusiss.paymentservice.dto.PaymentRequest;
import com.nusiss.paymentservice.dto.PaymentResult;

/*
 支付处理器接口
 定义所有支付方式的统一方法，便于使用工厂模式调用不同实现
 */
public interface PaymentProcessor {

    /*
     处理支付
     @param request 支付请求，包含 orderId, userId, amount, paymentMethod 等信息
     @return PaymentResult 返回支付结果(success, message)
     */
    PaymentResult processPayment(PaymentRequest request);
}
