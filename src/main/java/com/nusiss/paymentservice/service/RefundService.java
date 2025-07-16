package com.nusiss.paymentservice.service;

import com.nusiss.paymentservice.entity.Refund;
import java.util.List;

/*
 RefundService 接口用于定义退款功能相关的业务逻辑。
 包括创建退款、根据 paymentId 查询退款记录、查看所有退款记录等方法。
 */
public interface RefundService {

    /*
     创建退款记录

     @param refund Refund 对象，包含退款金额、原因、支付ID等信息
     @return 保存后的 Refund 实体
     */
    Refund createRefund(Refund refund);

    /*
     根据支付ID查询对应的退款记录

     @param paymentId 支付记录ID
     @return Refund 对象，若不存在则返回 null
     */
    Refund getRefundByPaymentId(Long paymentId);

    /*
     获取所有退款记录

     @return Refund 列表
     */
    List<Refund> getAllRefunds();
}
