package com.newspaper.System.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SubscribeRequestDTO {

    @NotNull(message = "Newspaper ID is required")
    private Integer newspaperId;

    @NotBlank(message = "Billing type is required")
    private String billingType; // DAILY or MONTHLY

    private String paymentMode;

    public Integer getNewspaperId() {
        return newspaperId;
    }

    public void setNewspaperId(Integer newspaperId) {
        this.newspaperId = newspaperId;
    }

    public String getBillingType() {
        return billingType;
    }

    public void setBillingType(String billingType) {
        this.billingType = billingType;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}