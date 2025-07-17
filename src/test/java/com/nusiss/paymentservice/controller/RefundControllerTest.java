package com.nusiss.paymentservice.controller;

import com.nusiss.paymentservice.config.ApiResponse;
import com.nusiss.paymentservice.entity.Refund;
import com.nusiss.paymentservice.service.RefundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RefundControllerTest {

    @InjectMocks
    private RefundController refundController;

    @Mock
    private RefundService refundService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 测试 createRefund 方法
    @Test
    public void testCreateRefund() {
        // Arrange
        Refund refund = new Refund();
        refund.setId(1L);
        when(refundService.createRefund(refund)).thenReturn(refund);

        // Act
        ApiResponse<Refund> response = refundController.createRefund(refund);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Refund created successfully", response.getMessage());
        assertEquals(refund, response.getData());
        verify(refundService, times(1)).createRefund(refund);
    }

    // 测试 getRefundByPaymentId 成功情况
    @Test
    public void testGetRefundByPaymentId_Exists() {
        // Arrange
        Long paymentId = 100L;
        Refund refund = new Refund();
        refund.setId(1L);
        refund.setPaymentId(paymentId);
        when(refundService.getRefundByPaymentId(paymentId)).thenReturn(refund);

        // Act
        ApiResponse<Refund> response = refundController.getRefundByPaymentId(paymentId);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Refund record found", response.getMessage());
        assertEquals(refund, response.getData());
        verify(refundService, times(1)).getRefundByPaymentId(paymentId);
    }

    // 测试 getRefundByPaymentId 不存在的情况
    @Test
    public void testGetRefundByPaymentId_NotExists() {
        // Arrange
        Long paymentId = 999L;
        when(refundService.getRefundByPaymentId(paymentId)).thenReturn(null);

        // Act
        ApiResponse<Refund> response = refundController.getRefundByPaymentId(paymentId);

        // Assert
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("No refund found for paymentId: " + paymentId, response.getMessage());
        assertNull(response.getData());
        verify(refundService, times(1)).getRefundByPaymentId(paymentId);
    }

    // 测试 getAllRefunds 成功情况
    @Test
    public void testGetAllRefunds() {
        // Arrange
        List<Refund> refunds = Arrays.asList(new Refund(), new Refund());
        when(refundService.getAllRefunds()).thenReturn(refunds);

        // Act
        ApiResponse<List<Refund>> response = refundController.getAllRefunds();

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("All refund records retrieved", response.getMessage());
        assertEquals(refunds, response.getData());
        verify(refundService, times(1)).getAllRefunds();
    }
}
