package com.example.spring_ai_demo.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Data
public class ExpenseInput {
    private LocalDate date;
    private float amount;
    private String description;
}