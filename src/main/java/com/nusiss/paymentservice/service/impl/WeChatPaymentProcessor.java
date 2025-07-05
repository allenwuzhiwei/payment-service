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
 微信支付处理器
 实现 PaymentProcessor 接口，处理微信支付逻辑
 */
@Component  // 注册为 Spring Bean，便于工厂注入
public class WeChatPaymentProcessor implements PaymentProcessor {

    @Autowired
    private MoneyAccountRepository moneyAccountRepository;

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // 1. 从 money_account 表获取用户 WeChat 账户
        MoneyAccount account = moneyAccountRepository
                .findByUserIdAndAccountType(request.getUserId(), "WeChat");

        if (account == null) {
            return new PaymentResult(false, "WeChat account not found");
        }

        // 2. 检查余额是否充足
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            return new PaymentResult(false, "Insufficient WeChat balance");
        }

        // 3. 扣减余额
        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        account.setBalance(newBalance);
        moneyAccountRepository.save(account);

        // 4. 返回成功结果
        return new PaymentResult(true, "WeChat payment successful");
    }
}
