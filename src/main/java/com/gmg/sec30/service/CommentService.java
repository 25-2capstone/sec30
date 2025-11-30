package com.gmg.sec30.service;

import com.gmg.sec30.dto.response.CommentResponseDto;
import lombok.RequiredArgsConstructor;
import com.gmg.sec30.entity.Playlist;
import com.gmg.sec30.entity.Comment;
import com.gmg.sec30.entity.User;
import com.gmg.sec30.dto.request.AddCommentRequestDto;
import com.gmg.sec30.dto.request.UpdateCommentRequestDto;
import com.gmg.sec30.repository.PlaylistRepository;
import com.gmg.sec30.repository.CommentRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PlaylistRepository PlaylistRepository;
    private final DomainLookupService lookup;

    public Comment save(int id, AddCommentRequestDto request, String userName) {
        Playlist playlist = PlaylistRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("댓글 쓰기 실패: 해당 게시글이 존재하지 않습니다. " + id));

        request.setAuthor(userName);
        request.setPlaylistId(playlist);

        return commentRepository.save(request.toEntity());
    }

    @Transactional(readOnly = true)
    public List<Comment> findAll(int id) {
        Playlist Playlist = PlaylistRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id: " + id));
        List<Comment> comments = Playlist.getComments();
        return comments;
    }

    @Transactional
    public Comment update(int id, UpdateCommentRequestDto request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found :" + id));

        authorizeCommentAuthor(comment);
        comment.update(request.getComment(), request.getUpdatedAt());

        return comment;
    }

    public void delete(int id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found :" + id));

        authorizeCommentAuthor(comment);
        commentRepository.delete(comment);
    }


    public Comment findById(int id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    public Comment saveReply(int playlistId, int parentId, AddCommentRequestDto request, String userName) {

        Playlist playlist = PlaylistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found"));

        Comment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));

        Comment reply = Comment.builder()
                .comment(request.getComment())
                .author(userName)
                .parent(parentComment)
                .playlistId(playlist)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return commentRepository.save(reply);
    }

    public List<CommentResponseDto> getUserComments(String username) {
        User user = lookup.requireUser(username);
        return commentRepository.findByAuthorOrderByCreatedAtDesc(username).stream()
                .map(CommentResponseDto::from)
                .collect(Collectors.toList());
    }

    private static void authorizeCommentAuthor(Comment comment) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!comment.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }
}
