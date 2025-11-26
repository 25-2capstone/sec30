package com.gmg.sec30.service;

import com.gmg.sec30.entity.User;
import com.gmg.sec30.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    @Transactional
    public User registerUser(String email, String nickname, String name, String password) {
        // 이메일 중복 체크
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 닉네임 중복 체크
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 사용자 생성 (createAt은 @CreatedDate로 자동 설정됨)
        User user = User.builder()
                .email(email)
                .nickname(nickname)
                .name(name)
                .password(encodedPassword)
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getEmail());

        return savedUser;
    }

    /**
     * 이메일로 사용자 조회
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    /**
     * 닉네임으로 사용자 조회
     */
    public User findByNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
}
