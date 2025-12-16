package com.example.spring_ai_demo.adapter.in.web.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
public class ClassificationResult {
    /**
     * 勘定科目コードと名前のマッピング情報。
     */
    private static final Map<String, AccountTitleInfo> ACCOUNT_TITLE_MAP;

    static {
        Map<String, AccountTitleInfo> map = new HashMap<>();
        map.put("701", new AccountTitleInfo("給料手当", "Salaries and Wages"));
        map.put("702", new AccountTitleInfo("消耗品費", "Supplies Expense"));
        map.put("703", new AccountTitleInfo("支払手数料", "Commissions Paid"));
        map.put("704", new AccountTitleInfo("旅費交通費", "Travel and Transportation"));
        map.put("705", new AccountTitleInfo("通信費", "Communication Expense"));
        map.put("706", new AccountTitleInfo("地代家賃", "Rent Expense"));
        map.put("707", new AccountTitleInfo("水道光熱費", "Utilities Expense"));
        map.put("708", new AccountTitleInfo("広告宣伝費", "Advertising Expense"));
        ACCOUNT_TITLE_MAP = Collections.unmodifiableMap(map);
    }

    @Data
    private static class AccountTitleInfo {
        private final String japaneseName;
        private final String englishName;

        public AccountTitleInfo(String japaneseName, String englishName) {
            this.japaneseName = japaneseName;
            this.englishName = englishName;
        }
    }

    private String categoryCode;

    @Setter(AccessLevel.PROTECTED)
    private String accountTitleJp;

    @Setter(AccessLevel.PROTECTED)
    private String accountTitleEn;

    public ClassificationResult(String categoryCode) {
        this.categoryCode = categoryCode;

        AccountTitleInfo info = ACCOUNT_TITLE_MAP.get(categoryCode);

        if (Objects.nonNull(info)) {
            this.accountTitleJp = info.getJapaneseName();
            this.accountTitleEn = info.getEnglishName();
        } else {
            this.accountTitleJp = "不明な科目コード";
            this.accountTitleEn = "Unknown Account Code";
        }
    }
}