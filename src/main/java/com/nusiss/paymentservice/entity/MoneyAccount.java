package com.nusiss.paymentservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "money_account")
public class MoneyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String accountType; // 账户类型 (WeChat / PayNow / PayLah / FaceRecognition)
    private BigDecimal balance; // 余额
    private String currency;

    private String createUser;
    private String updateUser;
    private LocalDateTime createDatetime;
    private LocalDateTime updateDatetime;
}
