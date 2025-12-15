package com.example.spring_ai_demo;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Spring Securityの認証・認可ルールを設定
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/public/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/login") // フロントエンドがPOSTする認証エンドポイント
                        // 自動生成されるデフォルトのログインページは使用しない
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("Authentication failed");
                        })
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        // デフォルトのリダイレクトを無効にし、401 Unauthorizedを返す
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // ログアウト用のPOSTエンドポイント
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .permitAll()
                );

        return http.build();
    }

    /**
     * パスワードエンコーダーの定義
     * 必須のBeanであり、通常はBCryptを使用します。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * UserDetailsServiceの定義（認証に使用するユーザー情報のロード）
     * 実際には、JPAなどを使ってDBからユーザー情報を取得する実装に置き換えます。
     * ここではテスト用のインメモリユーザーを定義します。
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // テスト用のユーザー定義
        // ユーザー名: user123, パスワード: pass1234
        var user123 = User.builder()
                .username("user123")
                // 実際のパスワードをエンコードして設定
                .password(passwordEncoder.encode("pass1234"))
                .roles("USER")
                .build();
        // 2人目のテストユーザー定義
        var user456 = User.builder()
                .username("user456")
                // 実際のパスワードをエンコードして設定
                .password(passwordEncoder.encode("pass5678"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user123, user456);
    }
}