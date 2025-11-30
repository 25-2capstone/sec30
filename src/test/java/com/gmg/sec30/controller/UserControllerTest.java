package com.gmg.sec30.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("UserController 통합 테스트")
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("회원가입 페이지 접근")
    void showRegisterForm() {
        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/register", String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("로그인 페이지 접근")
    void showLoginForm() {
        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/login", String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("트랙 페이지 접근 (로그인 없이)")
    void showTracksWithoutLogin() {
        // when
        ResponseEntity<String> response = restTemplate.getForEntity("/tracks", String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

