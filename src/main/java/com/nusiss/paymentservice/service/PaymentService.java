package com.nusiss.paymentservice.service;

import com.nusiss.paymentservice.config.ApiResponse;
import com.nusiss.paymentservice.dto.PaymentRequest;
import com.nusiss.paymentservice.entity.Payment;

public interface PaymentService {
    ApiResponse<Payment> processPayment(PaymentRequest request);
}
