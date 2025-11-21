package com.gmg.sec30.controller;

import com.gmg.sec30.dto.request.CommentRequestDto;
import com.gmg.sec30.dto.response.CommentResponseDto;
import com.gmg.sec30.service.CommentService;
import com.gmg.sec30.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final CommentService commentService;
    private final LikeService likeService;

    @PostMapping("/playlists/{playlistId}/comments")
    @ResponseBody
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long playlistId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CommentRequestDto dto) {
        CommentResponseDto comment = commentService.createComment(
                playlistId, userDetails.getUsername(), dto);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/comments/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(id, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/playlists/{playlistId}/comments")
    @ResponseBody
    public ResponseEntity<List<CommentResponseDto>> getPlaylistComments(@PathVariable Long playlistId) {
        List<CommentResponseDto> comments = commentService.getPlaylistComments(playlistId);
        return ResponseEntity.ok(comments);
    }

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
            @PathVariable Long playlistId,
            @AuthenticationPrincipal UserDetails userDetails) {
        likeService.toggleLike(playlistId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/playlists/{playlistId}/like/status")
    @ResponseBody
    public ResponseEntity<Boolean> isLiked(
            @PathVariable Long playlistId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean isLiked = likeService.isLiked(playlistId, userDetails.getUsername());
        return ResponseEntity.ok(isLiked);
    }
}

