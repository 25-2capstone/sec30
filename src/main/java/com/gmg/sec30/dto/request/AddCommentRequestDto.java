package com.gmg.sec30.dto.request;

import lombok.*;
import com.gmg.sec30.entity.Playlist;
import com.gmg.sec30.entity.Comment;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddCommentRequestDto {

    private String comment;
    private Comment parentId;
    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime modifiedDate = LocalDateTime.now();
    private String author;
    private Playlist playlistId;

    public Comment toEntity() {
        return Comment.builder()
                .comment(comment)
                .parent(parentId)
                .createdAt(createdDate)
                .updatedAt(modifiedDate)
                .author(author)
                .playlistId(playlistId)
                .build();
    }
}


