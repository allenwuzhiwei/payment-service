package com.nusiss.paymentservice.controller;

import com.nusiss.paymentservice.config.ApiResponse;
import com.nusiss.paymentservice.dto.PaymentRequest;
import com.nusiss.paymentservice.entity.Payment;
import com.nusiss.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 模拟支付接口：校验用户账户余额是否充足，若充足则扣款并写入支付记录
 @param request 请求体参数，包含 orderId、userId、金额、支付方式等
 @return ApiResponse<Payment> 返回支付状态和记录
 */

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public ApiResponse<Payment> process(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }
}
