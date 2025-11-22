package com.gmg.sec30.controller;

import com.gmg.sec30.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String showDefaultPage() {
        return "login";
    }

    @GetMapping("/mainpage")
    public String showMainPage() {
        return "mainpage";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String nickname) {
        userService.registerUser(username, password, nickname);
        return "redirect:/";
    }

    @GetMapping("/mypage")
    public String showMyPage() {
        return "mypage";
    }

    // [삭제] 로그아웃은 SecurityConfig에서 처리하므로 컨트롤러 메서드 제거

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
    public String showSearchResults(@RequestParam String query, Model model) {
        model.addAttribute("searchedQuery", query);

        // [수정] 서비스의 통합 검색 메서드를 사용하여 로직 간소화
        UserService.UserDto foundUser = userService.searchUserIntegrated(query);

        if (foundUser != null) {
            model.addAttribute("isFound", true);
            model.addAttribute("foundUser", foundUser);
        } else {
            model.addAttribute("isFound", false);
        }

        return "search_results";
    }
}