package com.nusiss.paymentservice.controller;

import com.nusiss.paymentservice.config.ApiResponse;
import com.nusiss.paymentservice.dto.PaymentRequest;
import com.nusiss.paymentservice.entity.Payment;
import com.nusiss.paymentservice.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    public void testProcessPayment_ShouldReturnApiResponse() throws Exception {
        // Arrange - 构造模拟返回
        Payment mockPayment = new Payment();
        mockPayment.setId(1L);
        mockPayment.setPaymentStatus("SUCCESS");

        ApiResponse<Payment> response = ApiResponse.success(mockPayment);

        when(paymentService.processPayment(any(PaymentRequest.class)))
                .thenReturn(response);

        // Act & Assert - 构造 JSON 请求体并验证响应
        mockMvc.perform(post("/payment/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":1,\"userId\":100,\"amount\":100.0,\"currency\":\"SGD\",\"method\":\"PayNow\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.paymentStatus").value("SUCCESS"));

        // 验证 service 被正确调用
        verify(paymentService, times(1)).processPayment(any(PaymentRequest.class));
    }
}
