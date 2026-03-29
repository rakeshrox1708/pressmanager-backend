package com.newspaper.System.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int subscriptionId;

    @ManyToOne
    private User user;

    @ManyToOne
    private Newspaper newspaper;

    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // ACTIVE / PAUSED

    private String billingType; // DAILY or MONTHLY

    // getters & setters

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(int subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Newspaper getNewspaper() {
        return newspaper;
    }

    public void setNewspaper(Newspaper newspaper) {
        this.newspaper = newspaper;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBillingType() {
        return billingType;
    }

    public void setBillingType(String billingType) {
        this.billingType = billingType;
    }
}

