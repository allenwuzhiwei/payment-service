package com.nusiss.paymentservice.service.impl;

import com.nusiss.paymentservice.config.ApiResponse;
import com.nusiss.paymentservice.dto.PaymentRequest;
import com.nusiss.paymentservice.entity.MoneyAccount;
import com.nusiss.paymentservice.entity.Payment;
import com.nusiss.paymentservice.repository.MoneyAccountRepository;
import com.nusiss.paymentservice.repository.PaymentRepository;
import com.nusiss.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private MoneyAccountRepository moneyAccountRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    /*
     支付处理逻辑：
     1）查找付款账户
     2）判断余额是否充足
     3）余额足够：扣款 + 写入支付记录
     4）余额不足：返回失败响应
     */
    @Override
    public ApiResponse<Payment> processPayment(PaymentRequest request) {
        // 1. 查找付款账户（根据 userId + 币种）
        Optional<MoneyAccount> accountOpt = moneyAccountRepository
                .findByUserIdAndCurrency(request.getUserId(), request.getCurrency());

        if (accountOpt.isEmpty()) {
            return ApiResponse.fail("未找到对应的付款账户");
        }

        MoneyAccount account = accountOpt.get();

        // 2. 检查账户余额是否充足
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            return ApiResponse.fail("账户余额不足，支付失败");
        }

        // 3. 扣减余额并更新账户
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        account.setUpdateUser("system");
        account.setUpdateDatetime(LocalDateTime.now());
        moneyAccountRepository.save(account);

        // 4. 写入支付记录（状态设为 PAID）
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setSenderAccountId(account.getId());
        payment.setReceiverAccountId(999L); // 默认平台收款账户
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setPaymentStatus("PAID");
        payment.setVerificationMethod(request.getMethod());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCreateUser("system");
        payment.setCreateDatetime(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        return ApiResponse.success(saved);
    }
}
