package com.nusiss.paymentservice.service.impl;

import com.nusiss.paymentservice.dto.PaymentRequest;
import com.nusiss.paymentservice.dto.PaymentResult;
import com.nusiss.paymentservice.entity.MoneyAccount;
import com.nusiss.paymentservice.repository.MoneyAccountRepository;
import com.nusiss.paymentservice.service.PaymentProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/*
 PayLah 支付处理器
 实现 PaymentProcessor 接口，处理 PayLah 支付逻辑
 */
@Component
public class PayLahPaymentProcessor implements PaymentProcessor {

    @Autowired
    private MoneyAccountRepository moneyAccountRepository;

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // 1. 从 money_account 表获取用户 PayLah 账户
        MoneyAccount account = moneyAccountRepository
                .findByUserIdAndAccountType(request.getUserId(), "PayLah");

        if (account == null) {
            return new PaymentResult(false, "PayLah account not found");
        }

        // 2. 检查余额是否充足
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            return new PaymentResult(false, "Insufficient PayLah balance");
        }

        // 3. 扣减余额
        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        account.setBalance(newBalance);
        moneyAccountRepository.save(account);

        // 4. 返回成功结果
        return new PaymentResult(true, "PayLah payment successful");
    }
}
