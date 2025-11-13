package com.gmg.sec30.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Spotify 트랙 정보를 담는 Entity 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Track {

    private String id;
    private String name;
    private String artist;
    private String album;
    private String imageUrl;
    private String previewUrl;
    private String spotifyUrl;
}

