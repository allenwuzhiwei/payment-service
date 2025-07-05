package com.nusiss.paymentservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/*
 人脸识别日志表
 */
@Entity
@Table(name = "face_detection_log")
@Data
public class FaceDetectionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;       // 用户ID

    private Long paymentId;    // 支付ID

    private String result;     // 识别结果（SUCCESS / FAIL）

    private LocalDateTime detectionTime; // 检测时间

    private Double confidenceScore; // 置信度

    private String createUser;
    private String updateUser;
    private LocalDateTime createDatetime;
    private LocalDateTime updateDatetime;
}
