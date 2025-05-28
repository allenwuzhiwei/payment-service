package com.nusiss.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nusiss.paymentservice.config.ApiResponse;
import com.nusiss.paymentservice.dto.PaymentRequest;
import com.nusiss.paymentservice.entity.Payment;
import com.nusiss.paymentservice.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@TestPropertySource(properties = "spring.cloud.config.enabled=false")
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testProcessPayment_Success() throws Exception {
        // 构造请求参数
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(1001L);
        request.setUserId(1L);
        request.setAmount(new BigDecimal("99.99"));
        request.setCurrency("CNY");
        request.setMethod("WeChat");

        // 构造响应数据
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrderId(1001L);
        payment.setSenderAccountId(2L);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setPaymentStatus("PAID");
        payment.setPaymentDate(LocalDateTime.now());

        ApiResponse<Payment> mockResponse = ApiResponse.success(payment);

        // 模拟服务返回
        Mockito.when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(mockResponse);

        // 执行接口测试
        mockMvc.perform(post("/payment/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.paymentStatus").value("PAID"))
                .andExpect(jsonPath("$.data.orderId").value(1001));
    }
}
