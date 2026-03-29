package com.newspaper.System.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class DeliveryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int deliveryId;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    private LocalDate deliveryDate;
    private String status; // DELIVERED / NOT_DELIVERED

    // getters & setters


    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
