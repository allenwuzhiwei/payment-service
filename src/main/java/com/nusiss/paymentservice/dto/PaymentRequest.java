package com.nusiss.paymentservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Long orderId;            // 订单ID
    private Long userId;             // 支付人ID
    private BigDecimal amount;       // 订单金额
    private String currency;         // 币种
    private String method;           // 支付方式（WeChat / PayNow）
}
