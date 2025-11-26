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
    private Integer id;
    private String content;
    private String username;
    private Integer playlistId;
    private String playlistTitle;
    private LocalDateTime createdAt;

    public static CommentResponseDto from(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getCommentId())
                .content(comment.getContent())
                .username(comment.getUser().getNickname())
                .playlistId(comment.getPlaylist().getPlaylistId())
                .playlistTitle(comment.getPlaylist().getPlaylistTitle())
                .createdAt(comment.getCreateAt())
                .build();
    }
}

