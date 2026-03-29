package com.newspaper.System.dto;

public class MonthlySummaryDTO {
    public int delivered;
    public int missed;

    public int getDelivered() {
        return delivered;
    }

    public void setDelivered(int delivered) {
        this.delivered = delivered;
    }

    public int getMissed() {
        return missed;
    }

    public void setMissed(int missed) {
        this.missed = missed;
    }
}