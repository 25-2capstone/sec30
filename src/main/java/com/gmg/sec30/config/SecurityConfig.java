package com.gmg.sec30.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 정적 리소스 전체 허용
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                // CSS, JS, 이미지 등 정적 파일
                .requestMatchers("/css/**", "/js/**", "/img/**", "/images/**").permitAll()
                // 메인/홈/에러/파비콘 공개
                .requestMatchers("/", "/home", "/error", "/favicon.ico").permitAll()
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
                .permitAll()
            )
            .logout(logout -> logout.permitAll())
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username("alex.johnson")
            .password(passwordEncoder().encode("password"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
