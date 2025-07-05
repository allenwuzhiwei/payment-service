package com.nusiss.paymentservice.repository;

import com.nusiss.paymentservice.entity.FaceDetectionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaceDetectionLogRepository extends JpaRepository<FaceDetectionLog, Long> {
    // 目前可使用默认方法
}
