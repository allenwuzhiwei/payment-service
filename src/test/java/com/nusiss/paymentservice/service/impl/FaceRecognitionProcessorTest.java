//package com.nusiss.paymentservice.service.impl;
//
//import com.nusiss.paymentservice.dto.PaymentRequest;
//import com.nusiss.paymentservice.dto.PaymentResult;
//import com.nusiss.paymentservice.entity.FaceDetectionLog;
//import com.nusiss.paymentservice.entity.MoneyAccount;
//import com.nusiss.paymentservice.repository.FaceDetectionLogRepository;
//import com.nusiss.paymentservice.repository.MoneyAccountRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.math.BigDecimal;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class FaceRecognitionProcessorTest {
//
//    @InjectMocks
//    private FaceRecognitionProcessor processor;
//
//    @Mock
//    private MoneyAccountRepository moneyAccountRepository;
//
//    @Mock
//    private FaceDetectionLogRepository faceDetectionLogRepository;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // 1. 账户不存在
//    @Test
//    void testProcessPayment_accountNotFound() {
//        PaymentRequest request = new PaymentRequest();
//        request.setUserId(1L);
//
//        when(moneyAccountRepository.findByUserIdAndAccountType(1L, "FaceRecognition")).thenReturn(null);
//
//        PaymentResult result = processor.processPayment(request);
//
//        assertFalse(result.isSuccess());
//        assertEquals("FaceRecognition account not found", result.getMessage());
//        verify(faceDetectionLogRepository, never()).save(any());
//    }
//
//    // 2. 人脸识别失败
//    @Test
//    void testProcessPayment_faceRecognitionFailed() {
//        PaymentRequest request = new PaymentRequest();
//        request.setUserId(2L);
//
//        MoneyAccount account = new MoneyAccount();
//        account.setBalance(new BigDecimal("100.00"));
//        when(moneyAccountRepository.findByUserIdAndAccountType(2L, "FaceRecognition")).thenReturn(account);
//
//        FaceRecognitionProcessor spyProcessor = Mockito.spy(processor);
//        doReturn(false).when(spyProcessor).mockFaceRecognitionAPI();
//
//        PaymentResult result = spyProcessor.processPayment(request);
//
//        assertFalse(result.isSuccess());
//        assertEquals("Face recognition failed", result.getMessage());
//        verify(faceDetectionLogRepository).save(any(FaceDetectionLog.class));
//        verify(moneyAccountRepository, never()).save(any());
//    }
//
//    // 3. 余额不足
//    @Test
//    void testProcessPayment_insufficientBalance() {
//        PaymentRequest request = new PaymentRequest();
//        request.setUserId(3L);
//        request.setAmount(new BigDecimal("200.00"));
//
//        MoneyAccount account = new MoneyAccount();
//        account.setBalance(new BigDecimal("100.00"));
//        when(moneyAccountRepository.findByUserIdAndAccountType(3L, "FaceRecognition")).thenReturn(account);
//
//        FaceRecognitionProcessor spyProcessor = Mockito.spy(processor);
//        doReturn(true).when(spyProcessor).mockFaceRecognitionAPI();
//
//        PaymentResult result = spyProcessor.processPayment(request);
//
//        assertFalse(result.isSuccess());
//        assertEquals("Insufficient FaceRecognition account balance", result.getMessage());
//        verify(moneyAccountRepository, never()).save(any());
//    }
//
//    // 4. 支付成功
//    @Test
//    void testProcessPayment_success() {
//        PaymentRequest request = new PaymentRequest();
//        request.setUserId(4L);
//        request.setAmount(new BigDecimal("50.00"));
//
//        MoneyAccount account = new MoneyAccount();
//        account.setBalance(new BigDecimal("100.00"));
//        when(moneyAccountRepository.findByUserIdAndAccountType(4L, "FaceRecognition")).thenReturn(account);
//
//        FaceRecognitionProcessor spyProcessor = Mockito.spy(processor);
//        doReturn(true).when(spyProcessor).mockFaceRecognitionAPI();
//
//        PaymentResult result = spyProcessor.processPayment(request);
//
//        assertTrue(result.isSuccess());
//        assertEquals("FaceRecognition payment successful", result.getMessage());
//        verify(moneyAccountRepository).save(any(MoneyAccount.class));
//        verify(faceDetectionLogRepository).save(any(FaceDetectionLog.class));
//    }
//}
