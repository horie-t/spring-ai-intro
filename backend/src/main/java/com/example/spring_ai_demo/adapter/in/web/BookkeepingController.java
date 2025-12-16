package com.example.spring_ai_demo.adapter.in.web;

import com.example.spring_ai_demo.adapter.in.web.dto.ClassificationResult;
import com.example.spring_ai_demo.adapter.in.web.dto.ExpenseInput;
import com.example.spring_ai_demo.adapter.out.saas.OpenAIChatService;
import com.example.spring_ai_demo.adapter.out.saas.dto.AccountTitle;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookkeepingController {
    private final OpenAIChatService chatService;

    public BookkeepingController(OpenAIChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/api/bookkeeping/expenses:classify")
    public ClassificationResult classifyExpenses(@RequestBody ExpenseInput expenseInput) {
        AccountTitle accountTitle = chatService.classifyExpenses(expenseInput.getDescription());
        return new ClassificationResult(accountTitle.getCategoryCode());
    }
}
