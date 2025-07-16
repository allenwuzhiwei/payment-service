package com.nusiss.paymentservice.repository;

import com.nusiss.paymentservice.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {

    /*
     根据 paymentId 查询退款记录
     */
    Optional<Refund> findByPaymentId(Long paymentId);
}
