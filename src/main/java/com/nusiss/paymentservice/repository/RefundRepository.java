package com.nusiss.paymentservice.repository;

import com.nusiss.paymentservice.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    // 目前可使用默认方法
}
