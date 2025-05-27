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
    private Long receiverAccountId;

    private BigDecimal amount;
    private String currency;

    private String paymentStatus; // 示例值：PAID
    private String verificationMethod;

    private LocalDateTime paymentDate;
    private String createUser;
    private String updateUser;
    private LocalDateTime createDatetime;
    private LocalDateTime updateDatetime;
}
