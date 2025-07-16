package com.nusiss.paymentservice.service.impl;

import com.nusiss.paymentservice.entity.Payment;
import com.nusiss.paymentservice.entity.MoneyAccount;

import com.nusiss.paymentservice.entity.Refund;
import com.nusiss.paymentservice.repository.MoneyAccountRepository;
import com.nusiss.paymentservice.repository.PaymentRepository;
import com.nusiss.paymentservice.repository.RefundRepository;
import com.nusiss.paymentservice.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/*
 RefundServiceImpl 实现类，处理退款逻辑。
 */
@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MoneyAccountRepository moneyAccountRepository;


    /*
     创建退款记录
     */
    @Override
    public Refund createRefund(Refund refund) {
        // 0. 防止重复退款：检查该 paymentId 是否已退款
        if (refundRepository.findByPaymentId(refund.getPaymentId()).isPresent()) {
            throw new RuntimeException("Refund already exists for this payment");
        }

        // 1. 查找 payment 记录
        Payment payment = paymentRepository.findById(refund.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Payment record not found"));

        // 2. 获取付款方账户 ID
        Long senderAccountId = payment.getSenderAccountId();

        // 3. 查找付款账户
        MoneyAccount senderAccount = moneyAccountRepository.findById(senderAccountId)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        // 4. 退款金额 = 支付金额
        BigDecimal refundAmount = payment.getAmount();
        senderAccount.setBalance(senderAccount.getBalance().add(refundAmount));
        moneyAccountRepository.save(senderAccount);

        // 5. 设置退款记录字段
        refund.setRefundAmount(refundAmount);     // 自动设置退款金额
        refund.setRefundStatus("REFUNDED");       // 可以自定义状态管理
        refund.setCreateUser("system");           // 默认系统创建
        refund.setCreateDatetime(java.time.LocalDateTime.now());

        return refundRepository.save(refund);
    }

    /*
     根据支付ID查询退款记录
     */
    @Override
    public Refund getRefundByPaymentId(Long paymentId) {
        return refundRepository.findByPaymentId(paymentId).orElse(null);
    }

    /*
     查询所有退款记录
     */
    @Override
    public List<Refund> getAllRefunds() {
        return refundRepository.findAll();
    }
}
