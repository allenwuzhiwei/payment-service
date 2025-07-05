package com.nusiss.paymentservice.controller;

import com.nusiss.paymentservice.config.ApiResponse;
import com.nusiss.paymentservice.dto.PaymentRequest;
import com.nusiss.paymentservice.entity.Payment;
import com.nusiss.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 PaymentController
 处理支付请求接口
 */
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /*
     处理支付
     POST /payment/process
     @param request PaymentRequest
     @return ApiResponse<Payment>
     */
    @PostMapping("/process")
    public ApiResponse<Payment> processPayment(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }
}
