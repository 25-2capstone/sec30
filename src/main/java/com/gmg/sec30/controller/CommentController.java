package com.gmg.sec30.controller;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.olaseo.springbootdeveloper.domain.Comment;
import me.olaseo.springbootdeveloper.dto.*;
import me.olaseo.springbootdeveloper.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;

    @GetMapping("/api/playlists/{playlistId}/comments/list")
    public String getCommentsList(@PathVariable Long id, Model model) {
        List<Comment> comments = commentService.findAll(id);
        model.addAttribute("comments", comments);
        return "fragments/comment-list";
    }

    @PostMapping("/api/playlists/{playlistId}/comments")
    @Transactional
    public ResponseEntity<Comment> save(@PathVariable Long id,
                                        @RequestBody AddCommentRequest request,
                                        Principal principal) {
        Comment savedComment = commentService.save(id, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }

    @PostMapping("/api/articles/playlists/{playlistId}/comments/reply")
    @Transactional
    public ResponseEntity<Comment> saveReply(@PathVariable Long playlistId,
                                             @PathVariable Long parentId,
                                             @RequestBody AddCommentRequest request,
                                             Principal principal) {
        Comment savedReply = commentService.saveReply(playlistId, parentId, request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReply);
    }

    @GetMapping("/api/articles/playlists/{playlistId}/comments/{id}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable Long playlistId,
                                                      @PathVariable Long id) {
        Comment comment = commentService.findById(id);
        return ResponseEntity.ok(new CommentResponse(comment));
    }

    @PatchMapping("/api/articles/playlists/{playlistId}/comments/{id}")
    @Transactional
    public ResponseEntity<Comment> updateComment(@PathVariable Long playlistId,
                                                 @PathVariable Long id,
                                                 @RequestBody UpdateCommentRequest request) {
        Comment updated = commentService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/api/articles/playlists/{playlistId}/comments/{id}")
    @Transactional
    public ResponseEntity<Void> deleteComment(@PathVariable Long articleId,
                                              @PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.ok().build();
    }
}

@Data
class CommentResponse {
    private String author;
    private String comment;

    public CommentResponse(Comment comment) {
        this.author = comment.getAuthor();
        this.comment = comment.getComment();
    }
}