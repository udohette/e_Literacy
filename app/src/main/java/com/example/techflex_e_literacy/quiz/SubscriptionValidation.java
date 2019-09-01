package com.example.techflex_e_literacy.quiz;

public class SubscriptionValidation {
    public String startDate;
    public String endDate;

    public SubscriptionValidation(){

    };

    public SubscriptionValidation(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
