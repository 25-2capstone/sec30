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
    @Column(name = "trackId", length = 255) // ERD와 동일
    private String trackId; // 외부 API에서 받는 값이므로 자동 생성 X

    @Column(name = "trackTitle", length = 30, nullable = false)
    private String trackTitle;

    @Column(name = "artistName", length = 30)
    private String artistName;

    @Column(name = "albumName", length = 30)
    private String albumName;

    // 참고: ERD에 length 30으로 되어있으나, URL을 저장하기엔 짧아 보입니다. 255로 수정했습니다.
    @Column(name = "previewUrl", length = 255)
    private String previewUrl;

    // 참고: ERD에 length 30으로 되어있으나, URL을 저장하기엔 짧아 보입니다. 255로 수정했습니다.
    @Column(name = "imageUri", length = 255)
    private String imageUri;

    // --- 연관관계 매핑 ---

    @Builder.Default
    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaylistTrack> playlistTracks = new ArrayList<>();
}