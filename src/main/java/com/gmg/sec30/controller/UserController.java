package com.gmg.sec30.controller;

import com.gmg.sec30.dto.response.CommentResponseDto;
import com.gmg.sec30.service.CommentService;
import com.gmg.sec30.service.LikeService;
import com.gmg.sec30.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final CommentService commentService;
    private final LikeService likeService;
    private final UserService userService;

    @GetMapping("/my/comments")
    @ResponseBody
    public ResponseEntity<List<CommentResponseDto>> getMyComments(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<CommentResponseDto> comments = commentService.getUserComments(userDetails.getUsername());
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/playlists/{playlistId}/like")
    @ResponseBody
    public ResponseEntity<Void> toggleLike(
            @PathVariable Integer playlistId,
            @AuthenticationPrincipal UserDetails userDetails) {
        likeService.toggleLike(playlistId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/playlists/{playlistId}/like/status")
    @ResponseBody
    public ResponseEntity<Boolean> isLiked(
            @PathVariable Integer playlistId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean isLiked = likeService.isLiked(playlistId, userDetails.getUsername());
        return ResponseEntity.ok(isLiked);
    }

    /**
     * 회원가입 처리
     */
    @PostMapping("/register")
    public String register(
            @RequestParam String email,
            @RequestParam String nickname,
            @RequestParam String name,
            @RequestParam String password,
            @RequestParam String passwordConfirm,
            RedirectAttributes redirectAttributes) {

        try {
            // 비밀번호 확인
            if (!password.equals(passwordConfirm)) {
                redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
                return "redirect:/register?error";
            }

            // 입력값 검증
            if (email == null || email.isBlank()) {
                redirectAttributes.addFlashAttribute("error", "이메일을 입력해주세요.");
                return "redirect:/register?error";
            }
            if (nickname == null || nickname.isBlank() || nickname.length() < 2 || nickname.length() > 20) {
                redirectAttributes.addFlashAttribute("error", "닉네임은 2-20자 이내로 입력해주세요.");
                return "redirect:/register?error";
            }
            if (name == null || name.isBlank()) {
                redirectAttributes.addFlashAttribute("error", "이름을 입력해주세요.");
                return "redirect:/register?error";
            }
            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "비밀번호는 최소 6자 이상이어야 합니다.");
                return "redirect:/register?error";
            }

            // 회원가입 처리
            userService.registerUser(email, nickname, name, password);
            log.info("User registration successful: {}", email);

            // 성공 시 로그인 페이지로 리다이렉트
            redirectAttributes.addFlashAttribute("success", "회원가입이 완료되었습니다. 로그인해주세요.");
            return "redirect:/login?success";

        } catch (IllegalArgumentException e) {
            log.warn("User registration failed: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register?error";
        } catch (Exception e) {
            log.error("Unexpected error during user registration", e);
            redirectAttributes.addFlashAttribute("error", "회원가입 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/register?error";
        }
    }
}

