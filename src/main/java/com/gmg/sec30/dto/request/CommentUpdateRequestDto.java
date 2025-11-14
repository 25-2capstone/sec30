package com.gmg.sec30.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateCommentRequest {
    private String comment;
    private LocalDateTime updatedAt;
}