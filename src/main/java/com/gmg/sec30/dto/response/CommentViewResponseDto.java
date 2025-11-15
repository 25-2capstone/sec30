package com.gmg.sec30.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
/*import com.gmg.sec30..domain.Playlist;*/
import com.gmg.sec30.domain.Comment;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class CommentViewResponseDto {
    private Long id;
    private String commentContent;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private Playlist playlistId;

    public CommentViewResponseDto (Comment comment) {
        this.id = comment.getId();
        this.commentContent = comment.getComment();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.playlistId = comment.getPlaylistId();
    }
}