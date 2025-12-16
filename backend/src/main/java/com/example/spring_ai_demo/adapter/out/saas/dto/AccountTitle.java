package com.example.spring_ai_demo.adapter.out.saas.dto;

import lombok.Getter;

@Getter
public enum AccountTitle {
    // Enum定数名は、そのまま英語表記（スネークケース）として利用する
    SALARIES_AND_WAGES("701"),
    SUPPLIES_EXPENSE("702"),
    COMMISSIONS_PAID("703"),
    TRAVEL_EXPENSE("704"),
    COMMUNICATION_EXPENSE("705"),
    RENT_EXPENSE("706"),
    UTILITIES_EXPENSE("707"),
    ADVERTISING_EXPENSE("708");

    private final String categoryCode; // 勘定科目コードのみ保持

    AccountTitle(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getEnglishNameAlternative() {
        // 例: SALARIES_AND_WAGES -> Salaries And Wages
        return name().replace("_", " ").toLowerCase();
    }
}