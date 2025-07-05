package com.nusiss.paymentservice.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 退款记录表
 */
@Entity
@Table(name = "refund")
@Data
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long paymentId;       // 关联的支付记录ID

    private BigDecimal refundAmount; // 退款金额

    private String refundReason;  // 退款原因

    private String refundStatus;  // 退款状态

    private LocalDateTime refundDate; // 退款日期

    private String createUser;
    private String updateUser;
    private LocalDateTime createDatetime;
    private LocalDateTime updateDatetime;
}
