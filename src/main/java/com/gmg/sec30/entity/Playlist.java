package com.gmg.sec30.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "playlist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE playlist SET delete_at = CURRENT_TIMESTAMP WHERE playlist_id = ?")
@Where(clause = "delete_at IS NULL")
public class Playlist extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playlist_id")
    private Integer playlistId;

    @Column(name = "playlist_title", length = 20, nullable = false)
    private String playlistTitle;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // 참고: ERD에 length 30으로 되어있으나, URL을 저장하기엔 짧아 보입니다. 255로 수정했습니다.
    @Column(name = "image_uri", length = 255)
    private String imageUri;

    // --- 연관관계 매핑 ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaylistTrack> playlistTracks = new ArrayList<>();
}