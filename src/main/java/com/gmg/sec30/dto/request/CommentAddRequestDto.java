package com.gmg.sec30.dto.request;

import lombok.*;
/*import com.gmg.sec30.domain.Playlist;*/
import com.gmg.sec30.domain.Comment;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddCommentRequest {

    private String comment;
    private Comment parentId;
    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime modifiedDate = LocalDateTime.now();
    private String author;
    private Article articleId;

    public Comment toEntity() {
        return Comment.builder()
                .comment(comment)
                .parent(parentId)
                .createdAt(createdDate)
                .updatedAt(modifiedDate)
                .author(author)
                .articleId(articleId)
                .build();
    }
}