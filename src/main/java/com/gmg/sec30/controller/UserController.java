package com.gmg.sec30.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    private record User(String username, String password, String nickname) {}
    private Map<String, User> userDatabase = new HashMap<>();

    public UserController() {
        userDatabase.put("user", new User("user", "1234", "nickname"));
    }

    @GetMapping("/")
    public String showDefaultPage() {
        return "login";
    }

    @GetMapping("/mainpage")
    public String showMainPage() {
        return "mainpage";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, @RequestParam String password) {
        User user = userDatabase.get(username);
        if (user != null && user.password().equals(password)) {
            return "mainpage";
        } else {
            return "redirect:/?error=true";
        }
    }
}