package com.nusiss.paymentservice.service;

import com.nusiss.paymentservice.config.ApiResponse;
import com.nusiss.paymentservice.dto.PaymentRequest;
import com.nusiss.paymentservice.entity.Payment;

/*
 PaymentService 接口
 定义支付服务的方法
 */
public interface PaymentService {

    /*
     处理支付请求
     @param request PaymentRequest
     @return ApiResponse<Payment>
     */
    ApiResponse<Payment> processPayment(PaymentRequest request);
}
