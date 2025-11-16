package com.gmg.sec30.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }


    @PostMapping("/register")
    public String handleRegister(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String nickname) { // 닉네임 파라미터 추가

        if (!userDatabase.containsKey(username)) {

            String finalNickname = (nickname == null || nickname.trim().isEmpty()) ? username : nickname;


            User newUser = new User(username, password, finalNickname);
            userDatabase.put(username, newUser);
            System.out.println("신규 회원 등록: " + newUser);
        }
        return "redirect:/";
    }

    @GetMapping("/mypage")
    public String showMyPage() {
        return "mypage";
    }

    @GetMapping("/logout")
    public String showLogoutPage() {
        return "logout";
    }

    @GetMapping("/playlist1")
    public String showPlaylist1() {
        return "playlist1";
    }

    @GetMapping("/playlist2")
    public String showPlaylist2() {
        return "playlist2";
    }

    @GetMapping("/search")
    public String showSearchPage() {
        return "search";
    }


    @GetMapping("/search/results")
    public String showSearchResults(@RequestParam String query, Model model) { // 파라미터 이름을 'query'로 변경

        model.addAttribute("searchedQuery", query); // 검색어를 모델에 추가

        User foundUser = null;


        if (userDatabase.containsKey(query)) {
            foundUser = userDatabase.get(query);
        } else {

            for (User user : userDatabase.values()) {
                if (user.nickname().equalsIgnoreCase(query)) {
                    foundUser = user;
                    break;
                }
            }
        }


        if (foundUser != null) {
            model.addAttribute("isFound", true);
            model.addAttribute("foundUser", foundUser); // User 객체 자체를 전달
        } else {
            model.addAttribute("isFound", false);
        }
        return "search_results";
    }
}