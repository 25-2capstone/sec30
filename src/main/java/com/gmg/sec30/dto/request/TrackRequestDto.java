package com.gmg.sec30.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackRequestDto {
    private String spotifyId;
    private String name;
    private String artist;
    private String album;
    private String albumImage;
    private Integer durationMs;
    private String previewUrl;
}

