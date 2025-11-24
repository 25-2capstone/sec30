package com.gmg.sec30.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 정적 리소스 전체 허용
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                // CSS, JS, 이미지 등 정적 파일
                .requestMatchers("/css/**", "/js/**", "/img/**", "/images/**").permitAll()
                // 메인/홈/에러/파비콘/로그인 공개
                .requestMatchers("/", "/home", "/error", "/favicon.ico", "/login").permitAll()
                // 공개 조회용 (음악 검색/인기 트랙/플레이리스트 목록/상세) - 로그인 없이 바로 감상 가능
                .requestMatchers(HttpMethod.GET,
                        "/tracks", "/tracks/**",
                        "/search", "/search/**",
                        "/playlist", "/playlist/*" // 플레이리스트 목록 및 상세 조회
                ).permitAll()
                // H2 콘솔 허용 (개발용)
                .requestMatchers("/h2-console/**").permitAll()
                // 보호 리소스: 마이페이지, 플레이리스트 생성/수정/삭제, 댓글, 좋아요
                .requestMatchers("/mypage/**").authenticated()
                .requestMatchers("/playlist/create", "/playlist/*/edit", "/playlist/*/delete").authenticated()
                .requestMatchers(HttpMethod.POST, "/playlist/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/playlist/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/playlist/**").authenticated()
                // 나머지는 허용
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login") // 커스텀 로그인 페이지
                .defaultSuccessUrl("/tracks", true) // 로그인 성공 시 트랙 페이지로 이동
                .failureUrl("/login?error") // 로그인 실패 시 에러 파라미터와 함께 로그인 페이지로
                .permitAll()
            )
            .logout(logout -> logout.permitAll())
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
