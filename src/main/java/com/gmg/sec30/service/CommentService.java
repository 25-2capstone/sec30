package com.gmg.sec30.service;

import lombok.RequiredArgsConstructor;
/*import com.gmg.sec30.domain.Playlist;*/
import com.gmg.sec30.domain.Comment;
import com.gmg.sec30.domain.User;
import com.gmg.sec30.dto.AddCommentRequest;
import com.gmg.sec30.dto.UpdateCommentRequest;
/*import com.gmg.sec30.repository.PlaylistRepository;*/
import com.gmg.sec30.repository.CommentRepository;
import com.gmg.sec30.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;

    public Comment save(Long id, AddCommentRequest request, String userName) {
        Playlist playlist = playlistRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("댓글 쓰기 실패: 해당 게시글이 존재하지 않습니다. " + id));

        request.setAuthor(userName);
        request.setPlaylistId(playlist);

        return commentRepository.save(request.toEntity());
    }

    @Transactional(readOnly = true)
    public List<Comment> findAll(Long id) {
        Playlist playlist = playlistRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id: " + id));
        List<Comment> comments = playlist.getComments();
        return comments;
    }

    @Transactional
    public Comment update(long id, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found :" + id));

        authorizeCommentAuthor(comment);
        comment.update(request.getComment(), request.getUpdatedAt());

        return comment;
    }

    public void delete(long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found :" + id));

        authorizeCommentAuthor(comment);
        commentRepository.delete(comment);
    }


    public Comment findById(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    public Comment saveReply(Long playlistId, Long parentId, AddCommentRequest request, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found"));

        Comment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));

        Comment reply = Comment.builder()
                .comment(request.getComment())
                .author(user.getNickname())
                .parent(parentComment)
                .playlistId(playlist)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return commentRepository.save(reply);
    }

    private static void authorizeCommentAuthor(Comment comment) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!comment.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }
}