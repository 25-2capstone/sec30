package com.gmg.sec30.config;

import com.gmg.sec30.entity.User;
import com.gmg.sec30.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 데모 계정 생성 (이미 존재하면 스킵)
        if (userRepository.findByNickname("alex.johnson").isEmpty()) {
            User demoUser = User.builder()
                    .email("alex.johnson@example.com")
                    .password(passwordEncoder.encode("password"))
                    .nickname("alex.johnson")
                    .name("Alex J")
                    .build();

            userRepository.save(demoUser);
            log.info("✅ Demo user created: alex.johnson / password");
        } else {
            log.info("✅ Demo user already exists: alex.johnson");
        }
    }
}

