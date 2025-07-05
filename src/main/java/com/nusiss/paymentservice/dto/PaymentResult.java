package com.nusiss.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 支付结果 DTO
 封装支付接口的返回结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResult {

    private boolean success;  // 支付是否成功
    private String message;   // 返回信息（例如：支付成功、余额不足）
}
