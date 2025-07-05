package com.nusiss.paymentservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long senderAccountId;
    private Long receiverAccountId; // 收款账户ID (后续可做商户账户)

    private BigDecimal amount;
    private String currency;

    private String paymentStatus; // 支付状态 示例值：PAID
    private String verificationMethod; // 支付方式 (WeChat / PayNow / FaceRecognition 等)

    private LocalDateTime paymentDate;
    private String createUser;
    private String updateUser;
    private LocalDateTime createDatetime;
    private LocalDateTime updateDatetime;
}
