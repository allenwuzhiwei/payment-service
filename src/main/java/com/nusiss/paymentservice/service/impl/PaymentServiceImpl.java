package com.nusiss.paymentservice.service.impl;

import com.nusiss.paymentservice.config.ApiResponse;
import com.nusiss.paymentservice.dto.PaymentRequest;
import com.nusiss.paymentservice.dto.PaymentResult;
import com.nusiss.paymentservice.entity.Payment;
import com.nusiss.paymentservice.repository.PaymentRepository;
import com.nusiss.paymentservice.service.PaymentProcessorFactory;
import com.nusiss.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentProcessorFactory paymentProcessorFactory;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public ApiResponse<Payment> processPayment(PaymentRequest request) {
        try {
            // 1. 通过工厂获取对应的 PaymentProcessor
            var processor = paymentProcessorFactory.createProcessor(request.getMethod());

            // 2. 调用 processor 处理支付
            PaymentResult result = processor.processPayment(request);

            // 3. 支付失败，返回失败 ApiResponse
            if (!result.isSuccess()) {
                return ApiResponse.fail(result.getMessage());
            }

            // 4. 支付成功，写入 payment 表
            Payment payment = new Payment();
            payment.setOrderId(request.getOrderId());
            payment.setSenderAccountId(1L); // 可根据 processor 返回结果设置 senderAccountId(默认测试账号1L) TODO: 根据 processor 获取 senderAccountId
            payment.setReceiverAccountId(999L); // 默认平台收款账户
            payment.setAmount(request.getAmount());
            payment.setCurrency(request.getCurrency());
            payment.setPaymentStatus("PAID");
            payment.setVerificationMethod(request.getMethod());
            payment.setPaymentDate(LocalDateTime.now());
            payment.setCreateUser("system");
            payment.setCreateDatetime(LocalDateTime.now());

            Payment saved = paymentRepository.save(payment);

            return ApiResponse.success(saved);

        } catch (IllegalArgumentException e) {
            return ApiResponse.fail("Unsupported payment method: " + request.getMethod());
        } catch (Exception e) {
            return ApiResponse.fail("Payment processing failed: " + e.getMessage());
        }
    }
}
