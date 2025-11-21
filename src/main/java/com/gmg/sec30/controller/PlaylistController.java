package com.gmg.sec30.controller;

import com.gmg.sec30.dto.response.CommentResponseDto;
import com.gmg.sec30.dto.response.PlaylistResponseDto;
import com.gmg.sec30.service.CommentService;
import com.gmg.sec30.service.LikeService;
import com.gmg.sec30.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 플레이리스트 화면 렌더링 컨트롤러
 * REST API는 UserController에서 처리
 */
@Controller
@RequestMapping("/playlist")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final CommentService commentService;
    private final LikeService likeService;

    /**
     * 플레이리스트 목록 페이지
     */
    @GetMapping
    public String playlistList(Model model) {
        model.addAttribute("title", "플레이리스트");
        List<PlaylistResponseDto> playlists = playlistService.getPopularPlaylists(PageRequest.of(0, 20));
        model.addAttribute("playlists", playlists);
        return "playlist/list";
    }

    /**
     * 플레이리스트 상세 페이지
     */
    @GetMapping("/{id}")
    public String playlistDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        model.addAttribute("title", "플레이리스트 상세");

        // TODO: 플레이리스트 상세 정보 조회
        // PlaylistResponseDto playlist = playlistService.getPlaylistById(id);
        // model.addAttribute("playlist", playlist);

        // 댓글 목록 조회
        List<CommentResponseDto> comments = commentService.getPlaylistComments(id);
        model.addAttribute("comments", comments);

        // 좋아요 여부 확인 (로그인한 경우에만)
        if (userDetails != null) {
            boolean isLiked = likeService.isLiked(id, userDetails.getUsername());
            model.addAttribute("isLiked", isLiked);
        } else {
            model.addAttribute("isLiked", false);
        }

        return "playlist/detail";
    }

    /**
     * 플레이리스트 생성 폼 페이지
     */
    @GetMapping("/create")
    public String createPlaylistForm(Model model) {
        model.addAttribute("title", "플레이리스트 만들기");
        return "playlist/create";
    }

    /**
     * 플레이리스트 생성 처리
     */
    @PostMapping("/create")
    public String createPlaylist(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @AuthenticationPrincipal UserDetails userDetails) {
        // TODO: 플레이리스트 생성 로직
        // playlistService.createPlaylist(name, description, userDetails.getUsername());
        return "redirect:/mypage";
    }
}

