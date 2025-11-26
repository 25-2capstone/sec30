package com.gmg.sec30.controller;

import com.gmg.sec30.dto.response.CommentResponseDto;
import com.gmg.sec30.service.CommentService;
import com.gmg.sec30.service.LikeService;
import com.gmg.sec30.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 마이페이지 컨트롤러
 * 사용자의 플레이리스트, 좋아요, 댓글 등을 조회
 */
@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final PlaylistService playlistService;
    private final LikeService likeService;
    private final CommentService commentService;

    @GetMapping
    public String mypage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("title", "내 라이브러리");

        if (userDetails != null) {
            String username = userDetails.getUsername();

            try {
                // 내 플레이리스트 조회
                System.out.println("[MyPageController] 사용자 " + username + "의 플레이리스트 조회 시작");
                List<com.gmg.sec30.dto.response.PlaylistResponseDto> myPlaylists = playlistService.getUserPlaylists(username);
                System.out.println("[MyPageController] 조회된 플레이리스트 수: " + myPlaylists.size());
                model.addAttribute("myPlaylists", myPlaylists);
                model.addAttribute("playlistCount", myPlaylists.size());

                // 좋아요한 플레이리스트 조회
                // TODO: LikeService에 getLikedPlaylists 메서드 추가 필요
                // List<PlaylistResponseDto> likedPlaylists = likeService.getLikedPlaylists(username);
                // model.addAttribute("likedPlaylists", likedPlaylists);
                model.addAttribute("likeCount", 0);

                // 내가 작성한 댓글 조회
                List<CommentResponseDto> myComments = commentService.getUserComments(username);
                model.addAttribute("myComments", myComments);
                model.addAttribute("commentCount", myComments.size());
            } catch (RuntimeException e) {
                // 사용자 데이터가 없을 경우 기본값으로 처리
                System.out.println("Warning: User data not found for " + username + " - " + e.getMessage());
                e.printStackTrace();
                model.addAttribute("playlistCount", 0);
                model.addAttribute("likeCount", 0);
                model.addAttribute("commentCount", 0);
                model.addAttribute("myPlaylists", java.util.Collections.emptyList());
            }
        } else {
            // 로그인하지 않은 경우 기본값
            model.addAttribute("playlistCount", 0);
            model.addAttribute("likeCount", 0);
            model.addAttribute("commentCount", 0);
        }

        return "mypage/index";
    }
}

