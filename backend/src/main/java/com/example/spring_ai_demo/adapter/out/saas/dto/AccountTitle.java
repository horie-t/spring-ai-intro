package com.example.spring_ai_demo.adapter.out.saas.dto;

import lombok.Getter;

@Getter
public enum AccountTitle {
    SALARIES_AND_WAGES("701"),
    SUPPLIES_EXPENSE("702"),
    COMMISSIONS_PAID("703"),
    TRAVEL_EXPENSE("704"),
    COMMUNICATION_EXPENSE("705"),
    RENT_EXPENSE("706"),
    UTILITIES_EXPENSE("707"),
    ADVERTISING_EXPENSE("708");

    private final String categoryCode;

    AccountTitle(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getEnglishNameAlternative() {
        return name().replace("_", " ").toLowerCase();
    }
}