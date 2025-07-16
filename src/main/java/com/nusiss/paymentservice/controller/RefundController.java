package com.nusiss.paymentservice.controller;

import com.nusiss.paymentservice.config.ApiResponse;
import com.nusiss.paymentservice.entity.Refund;
import com.nusiss.paymentservice.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 RefundController 提供退款相关接口
 */
@RestController
@RequestMapping("/payments/refunds")
public class RefundController {

    @Autowired
    private RefundService refundService;

    /*
     创建退款接口
     POST /payments/refunds
     */
    @PostMapping
    public ApiResponse<Refund> createRefund(@RequestBody Refund refund) {
        Refund created = refundService.createRefund(refund);
        return ApiResponse.success("Refund created successfully", created);
    }

    /*
     根据 paymentId 查询退款记录
     GET /payments/refunds/payment/{paymentId}
     */
    @GetMapping("/payment/{paymentId}")
    public ApiResponse<Refund> getRefundByPaymentId(@PathVariable Long paymentId) {
        Refund refund = refundService.getRefundByPaymentId(paymentId);
        if (refund != null) {
            return ApiResponse.success("Refund record found", refund);
        } else {
            return ApiResponse.fail("No refund found for paymentId: " + paymentId);
        }
    }

    /*
     获取所有退款记录
     GET /payments/refunds
     */
    @GetMapping
    public ApiResponse<List<Refund>> getAllRefunds() {
        List<Refund> list = refundService.getAllRefunds();
        return ApiResponse.success("All refund records retrieved", list);
    }
}
