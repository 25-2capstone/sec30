package com.gmg.sec30.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "track")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Track {

    @Id
    @Column(name = "track_id", length = 255) // ERD와 동일
    private String trackId; // 외부 API에서 받는 값이므로 자동 생성 X

    @Column(name = "track_title", length = 30, nullable = false)
    private String trackTitle;

    @Column(name = "artist_name", length = 30)
    private String artistName;

    @Column(name = "album_name", length = 30)
    private String albumName;

    // 참고: ERD에 length 30으로 되어있으나, URL을 저장하기엔 짧아 보입니다. 255로 수정했습니다.
    @Column(name = "preview_url", length = 255)
    private String previewUrl;

    // 참고: ERD에 length 30으로 되어있으나, URL을 저장하기엔 짧아 보입니다. 255로 수정했습니다.
    @Column(name = "image_uri", length = 255)
    private String imageUri;

    // --- 연관관계 매핑 ---

    @Builder.Default
    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaylistTrack> playlistTracks = new ArrayList<>();

    // --- Thymeleaf 템플릿 호환성을 위한 메서드 ---
    public String getName() {
        return this.trackTitle;
    }

    public String getArtist() {
        return this.artistName;
    }

    public String getAlbum() {
        return this.albumName;
    }

    public String getAlbumImage() {
        return this.imageUri;
    }

    public String getSpotifyId() {
        return this.trackId;
    }
}