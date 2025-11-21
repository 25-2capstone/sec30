package com.gmg.sec30.dto.response;

import com.gmg.sec30.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private Long id;
    private String content;
    private String username;
    private Long playlistId;
    private String playlistTitle;
    private LocalDateTime createdAt;

    public static CommentResponseDto from(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .username(comment.getUser().getUsername())
                .playlistId(comment.getPlaylist().getId())
                .playlistTitle(comment.getPlaylist().getTitle())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}

