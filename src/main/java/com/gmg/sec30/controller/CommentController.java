package com.gmg.sec30.controller;

import com.gmg.sec30.dto.request.AddCommentRequestDto;
import com.gmg.sec30.dto.request.UpdateCommentRequestDto;
import com.gmg.sec30.dto.response.CommentResponseDto;
import com.gmg.sec30.entity.Comment;
import com.gmg.sec30.service.CommentService;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/playlist")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // Fragment로 댓글 목록 반환
    @GetMapping("/{id}/comments/list")
    public String getCommentsList(@PathVariable Integer id, Model model) {
        List<Comment> comments = commentService.findAll(id);
        model.addAttribute("comments", comments);  // ← 필수!
        return "fragments/comment-list";
    }

    @PostMapping("/{id}/comments")
    @Transactional
    public ResponseEntity<Comment> save(
            @PathVariable Integer id,
            @RequestBody AddCommentRequestDto request,
            Principal principal) {
        Comment savedComment = commentService.save(id, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    @PostMapping("/{playlistId}/comments/{parentId}/reply")
    @Transactional
    public ResponseEntity<Comment> saveReply(
            @PathVariable Integer playlistId,
            @PathVariable Integer parentId,
            @RequestBody AddCommentRequestDto request,
            Principal principal) {
        Comment savedReply = commentService.saveReply(playlistId, parentId, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReply);
    }

    @PatchMapping("/{playlistId}/comments/{id}")
    @Transactional
    public ResponseEntity<Comment> updateComment(
            @PathVariable Integer playlistId,
            @PathVariable Integer id,
            @RequestBody UpdateCommentRequestDto request) {
        Comment updated = commentService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{playlistId}/comments/{id}")
    public ResponseEntity<CommentResponse> getComment(
            @PathVariable Integer playlistId,
            @PathVariable Integer id) {
        Comment comment = commentService.findById(id);
        return ResponseEntity.ok(new CommentResponse(comment));
    }

    @DeleteMapping("/{playlistId}/comments/{id}")
    @Transactional
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer playlistId,
            @PathVariable Integer id) {
        commentService.delete(id);
        return ResponseEntity.ok().build();
    }
}

@Data
class CommentResponse {
    private String nickname;
    private String comment;

    public CommentResponse(Comment comment) {
        this.nickname = comment.getAuthor();
        this.comment = comment.getComment();
    }
}
