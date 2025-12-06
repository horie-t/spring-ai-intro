package com.example.spring_ai_demo.adapter.in.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper wrapper =
                new ContentCachingRequestWrapper(request);

        filterChain.doFilter(wrapper, response);

        // 読み込まれた後でもボディを取り出せる
        String body = new String(
                wrapper.getContentAsByteArray(),
                request.getCharacterEncoding()
        );

        logger.info("REQUEST BODY: " + body);
    }
}
