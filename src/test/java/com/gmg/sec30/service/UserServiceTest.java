package com.gmg.sec30.service;

import com.gmg.sec30.entity.User;
import com.gmg.sec30.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void registerUser_Success() {
        // given
        String email = "test@example.com";
        String nickname = "tester";
        String name = "테스터";
        String password = "password123";
        String encodedPassword = "encodedPassword";

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());
        given(userRepository.findByNickname(nickname)).willReturn(Optional.empty());
        given(passwordEncoder.encode(password)).willReturn(encodedPassword);

        User savedUser = User.builder()
                .userId(1)
                .email(email)
                .nickname(nickname)
                .name(name)
                .password(encodedPassword)
                .build();

        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when
        User result = userService.registerUser(email, nickname, name, password);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getNickname()).isEqualTo(nickname);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getPassword()).isEqualTo(encodedPassword);

        verify(userRepository).findByEmail(email);
        verify(userRepository).findByNickname(nickname);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("이메일 중복 시 회원가입 실패")
    void registerUser_DuplicateEmail() {
        // given
        String email = "duplicate@example.com";
        User existingUser = User.builder()
                .email(email)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(existingUser));

        // when & then
        assertThatThrownBy(() ->
            userService.registerUser(email, "nickname", "name", "password")
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이미 사용 중인 이메일입니다.");
    }

    @Test
    @DisplayName("닉네임 중복 시 회원가입 실패")
    void registerUser_DuplicateNickname() {
        // given
        String nickname = "duplicate";
        User existingUser = User.builder()
                .nickname(nickname)
                .build();

        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userRepository.findByNickname(nickname)).willReturn(Optional.of(existingUser));

        // when & then
        assertThatThrownBy(() ->
            userService.registerUser("email@test.com", nickname, "name", "password")
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("이미 사용 중인 닉네임입니다.");
    }

    @Test
    @DisplayName("이메일로 사용자 조회 성공")
    void findByEmail_Success() {
        // given
        String email = "find@example.com";
        User user = User.builder()
                .email(email)
                .nickname("finder")
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // when
        User result = userService.findByEmail(email);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("존재하지 않는 이메일 조회 시 예외 발생")
    void findByEmail_NotFound() {
        // given
        String email = "notfound@example.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findByEmail(email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("닉네임으로 사용자 조회 성공")
    void findByNickname_Success() {
        // given
        String nickname = "tester";
        User user = User.builder()
                .nickname(nickname)
                .email("test@example.com")
                .build();

        given(userRepository.findByNickname(nickname)).willReturn(Optional.of(user));

        // when
        User result = userService.findByNickname(nickname);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getNickname()).isEqualTo(nickname);
        verify(userRepository).findByNickname(nickname);
    }
}

