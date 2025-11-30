package com.gmg.sec30.dto.response;

import com.gmg.sec30.entity.Comment;
import com.gmg.sec30.entity.Playlist;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Builder
public class CommentResponseDto {
    private Integer id;
    private String comment;
    private String author;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private Playlist playlistId;
    private Playlist playlistName;

    /*public CommentResponseDto (Comment comment) {
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.playlistId = comment.getPlaylistId();
    }*/

    public static CommentResponseDto from(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .author(comment.getAuthor())
                .playlistId(comment.getPlaylistId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}

