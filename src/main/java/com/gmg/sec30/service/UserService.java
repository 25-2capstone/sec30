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

    public record UserDto(String username, String password, String nickname) {}
    private final Map<String, UserDto> userDatabase = new HashMap<>();

    private final PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;

        String encodedPassword = passwordEncoder.encode("1234");
        userDatabase.put("user", new UserDto("user", encodedPassword, "기본유저"));

        String encodedAdminPassword = passwordEncoder.encode("admin");
        userDatabase.put("admin", new UserDto("admin", encodedAdminPassword, "관리자"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto userDto = userDatabase.get(username);

        if (userDto == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return User.builder()
                .username(userDto.username())
                .password(userDto.password())
                .roles("USER")
                .build();
    }

    public boolean registerUser(String username, String password, String nickname) {
        if (userDatabase.containsKey(username)) {
            return false;
        }

        String finalNickname = (nickname == null || nickname.trim().isEmpty()) ? username : nickname;
        String encodedPassword = passwordEncoder.encode(password);

        UserDto newUser = new UserDto(username, encodedPassword, finalNickname);
        userDatabase.put(username, newUser);
        return true;
    }

    public UserDto findByUsername(String username) {
        return userDatabase.get(username);
    }

    public UserDto findByNickname(String nickname) {
        for (UserDto user : userDatabase.values()) {
            if (user.nickname().equalsIgnoreCase(nickname)) {
                return user;
            }
        }
        return null;
    }
}
