package com.newspaper.System.dto.request;

public class SubscriptionPaymentDTO {

    private int newspaperId;
    private int userId;
    private String orderId;
    private String paymentId;
    private String signature;

    // ✅ Getters
    public int getNewspaperId() {
        return newspaperId;
    }

    public int getUserId() {
        return userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getSignature() {
        return signature;
    }

    // ✅ Setters
    public void setNewspaperId(int newspaperId) {
        this.newspaperId = newspaperId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}