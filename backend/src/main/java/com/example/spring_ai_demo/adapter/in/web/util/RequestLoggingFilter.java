package com.example.spring_ai_demo.adapter.in.web.util;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RequestLoggingFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        // POST / PUT / PATCH だけログりたい場合はここでフィルタ
        HttpMethod method = request.getMethod();
        if (method == null ||
                !(HttpMethod.POST.equals(method)
                        || HttpMethod.PUT.equals(method)
                        || HttpMethod.PATCH.equals(method))) {

            return chain.filter(exchange);
        }

        // body を全部結合して1つの DataBuffer に
        return DataBufferUtils.join(request.getBody())
                .flatMap(dataBuffer -> {
                    try {
                        byte[] bodyBytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bodyBytes);
                        // DataBuffer を解放
                        DataBufferUtils.release(dataBuffer);

                        String bodyString = new String(bodyBytes, StandardCharsets.UTF_8);

                        // ログ出力（必要に応じてマスク処理など入れる）
                        log.info("Request body: {}", bodyString);

                        // 読み終わった body をもう一度下流に渡せるように再生成
                        Flux<DataBuffer> cachedBodyFlux =
                                Flux.defer(() -> {
                                    DataBuffer buffer = exchange.getResponse()
                                            .bufferFactory()
                                            .wrap(bodyBytes);
                                    return Mono.just(buffer);
                                });

                        ServerHttpRequest decoratedRequest =
                                new ServerHttpRequestDecorator(request) {
                                    @Override
                                    public Flux<DataBuffer> getBody() {
                                        return cachedBodyFlux;
                                    }
                                };

                        // request を差し替えた exchange にして chain へ
                        return chain.filter(exchange.mutate()
                                .request(decoratedRequest)
                                .build());

                    } catch (Exception e) {
                        // 何かあってもアプリ自体は動かす
                        log.warn("Failed to log request body", e);
                        return chain.filter(exchange);
                    }
                });
    }
}
