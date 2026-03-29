package com.newspaper.System.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Newspaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int newspaperId;

    private String name;
    private String language;
    private double dailyPrice;     // ₹ per day
    private double monthlyPrice;   // ₹ fixed per month

    // getters & setters

    public int getNewspaperId() {
        return newspaperId;
    }

    public void setNewspaperId(int newspaperId) {
        this.newspaperId = newspaperId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public double getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(double dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public double getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(double monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }
}

