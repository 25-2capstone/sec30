package com.gmg.sec30.service;

import com.gmg.sec30.dto.request.CommentRequestDto;
import com.gmg.sec30.dto.response.CommentResponseDto;
import com.gmg.sec30.entity.Comment;
import com.gmg.sec30.entity.Playlist;
import com.gmg.sec30.entity.User;
import com.gmg.sec30.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final DomainLookupService lookup;

    @Transactional
    public CommentResponseDto createComment(Integer playlistId, String username, CommentRequestDto dto) {
        User user = lookup.requireUser(username);
        Playlist playlist = lookup.requirePlaylist(playlistId);

        Comment comment = Comment.builder()
                .user(user)
                .playlist(playlist)
                .content(dto.getContent())
                .build();

        return CommentResponseDto.from(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Integer id, String username) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getNickname().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        commentRepository.delete(comment);
    }

    public List<CommentResponseDto> getPlaylistComments(Integer playlistId) {
        Playlist playlist = lookup.requirePlaylist(playlistId);
        return commentRepository.findByPlaylistOrderByCreateAtDesc(playlist).stream()
                .map(CommentResponseDto::from)
                .collect(Collectors.toList());
    }

    public List<CommentResponseDto> getUserComments(String username) {
        User user = lookup.requireUser(username);
        return commentRepository.findByUserOrderByCreateAtDesc(user).stream()
                .map(CommentResponseDto::from)
                .collect(Collectors.toList());
    }
}
