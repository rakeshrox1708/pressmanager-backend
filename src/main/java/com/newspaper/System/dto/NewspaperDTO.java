package com.newspaper.System.dto;

public class NewspaperDTO {
    public int newspaperId;
    public String name;
    public String language;
    public double dailyPrice;
    public double monthlyPrice;

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

    public int getNewspaperId() {
        return newspaperId;
    }

    public void setNewspaperId(int newspaperId) {
        this.newspaperId = newspaperId;
    }
}