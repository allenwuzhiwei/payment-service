package com.nusiss.paymentservice.service.impl;

import com.nusiss.paymentservice.dto.PaymentRequest;
import com.nusiss.paymentservice.dto.PaymentResult;
import com.nusiss.paymentservice.entity.FaceDetectionLog;
import com.nusiss.paymentservice.entity.MoneyAccount;
import com.nusiss.paymentservice.repository.FaceDetectionLogRepository;
import com.nusiss.paymentservice.repository.MoneyAccountRepository;
import com.nusiss.paymentservice.service.PaymentProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 人脸识别支付处理器
 实现 PaymentProcessor 接口，处理 FaceRecognition 支付逻辑
 当前为 stub 方法，模拟返回成功，可后续接入真实人脸识别 API
 */
@Component
public class FaceRecognitionProcessor implements PaymentProcessor {

    @Autowired
    private MoneyAccountRepository moneyAccountRepository;

    @Autowired
    private FaceDetectionLogRepository faceDetectionLogRepository;

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // 1. 从 money_account 表获取用户 FaceRecognition 账户
        MoneyAccount account = moneyAccountRepository
                .findByUserIdAndAccountType(request.getUserId(), "FaceRecognition");

        if (account == null) {
            return new PaymentResult(false, "FaceRecognition account not found");
        }

        // 2. 模拟调用人脸识别接口
        boolean faceVerified = mockFaceRecognitionAPI();

        // 3. 写入 face_detection_log 表
        FaceDetectionLog log = new FaceDetectionLog();
        log.setUserId(request.getUserId());
        log.setPaymentId(null); // 当前未生成支付记录，可后续更新
        log.setResult(faceVerified ? "SUCCESS" : "FAIL");
        log.setDetectionTime(LocalDateTime.now());
        log.setConfidenceScore(faceVerified ? 0.95 : 0.3); // mock 置信度
        log.setCreateDatetime(LocalDateTime.now());
        log.setUpdateDatetime(LocalDateTime.now());
        faceDetectionLogRepository.save(log);

        // 4. 若人脸验证失败，返回失败
        if (!faceVerified) {
            return new PaymentResult(false, "Face recognition failed");
        }

        // 5. 检查余额是否充足
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            return new PaymentResult(false, "Insufficient FaceRecognition account balance");
        }

        // 6. 扣减余额
        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        account.setBalance(newBalance);
        moneyAccountRepository.save(account);

        // 7. 返回成功结果
        return new PaymentResult(true, "FaceRecognition payment successful");
    }

    /*
     模拟调用人脸识别 API
     @return boolean
     */
    protected boolean mockFaceRecognitionAPI() {
        // 模拟返回 true
        return true;
    }
}
