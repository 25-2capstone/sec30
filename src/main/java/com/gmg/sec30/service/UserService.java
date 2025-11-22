package com.gmg.sec30.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {

    // [변경] 권한(role) 필드를 추가했습니다.
    public record UserDto(String username, String password, String nickname, String role) {}

    private final Map<String, UserDto> userDatabase = new HashMap<>();
    // [추가] 닉네임 검색 속도 O(1) 개선을 위한 별도 저장소
    private final Map<String, UserDto> nicknameDatabase = new HashMap<>();

    private final PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        initData();
    }

    private void initData() {
        // [변경] 초기 데이터 생성 시 Role 부여 및 두 Map에 모두 저장하는 헬퍼 메서드 사용
        saveUserInternal("user", "1234", "기본유저", "USER");
        saveUserInternal("admin", "admin", "관리자", "ADMIN");
    }

    // 내부적으로 사용하는 저장 헬퍼 메서드 (중복 코드 제거 및 데이터 일관성 유지)
    private void saveUserInternal(String username, String password, String nickname, String role) {
        String encodedPassword = passwordEncoder.encode(password);
        UserDto newUser = new UserDto(username, encodedPassword, nickname, role);

        userDatabase.put(username, newUser);
        nicknameDatabase.put(nickname, newUser); // 닉네임 맵에도 저장
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto userDto = userDatabase.get(username);

        if (userDto == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // [변경] 하드코딩된 "USER" 대신 저장된 실제 권한(role)을 사용
        return User.builder()
                .username(userDto.username())
                .password(userDto.password())
                .roles(userDto.role())
                .build();
    }

    // 회원가입 로직
    public boolean registerUser(String username, String password, String nickname) {
        if (userDatabase.containsKey(username)) {
            return false;
        }

        String finalNickname = (nickname == null || nickname.trim().isEmpty()) ? username : nickname;

        // 일반 회원가입은 기본적으로 "USER" 권한 부여
        // 패스워드 암호화는 saveUserInternal 안에서 수행하므로 여기선 평문 전달
        saveUserInternal(username, password, finalNickname, "USER");

        return true;
    }

    public UserDto findByUsername(String username) {
        return userDatabase.get(username);
    }

    // [변경] O(N) 반복문 제거 -> O(1) Map 조회로 성능 개선
    public UserDto findByNickname(String nickname) {
        return nicknameDatabase.get(nickname);
    }

    // [추가] 통합 검색 메서드 (컨트롤러 로직 단순화)
    public UserDto searchUserIntegrated(String query) {
        // 1. 아이디로 검색
        UserDto found = userDatabase.get(query);

        // 2. 없으면 닉네임으로 검색 (Map 조회라 빠름)
        if (found == null) {
            found = nicknameDatabase.get(query);
        }
        return found;
    }
}