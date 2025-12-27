package com.example.spring_ai_demo.adapter.out.persistence.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "社内ドキュメントを検索するためのリクエスト")
public record DocumentSearchRequest(
        @Schema(description = "検索したい内容や質問。例：'出張旅費の精算方法について'")
        String query
) {}
