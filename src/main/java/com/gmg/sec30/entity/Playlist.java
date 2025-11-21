package com.gmg.sec30.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "playlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    private String coverImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PlaylistTrack> playlistTracks = new ArrayList<>();

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Like> likes = new ArrayList<>();

    public int getTrackCount() {
        return playlistTracks != null ? playlistTracks.size() : 0;
    }

    public int getLikeCount() {
        return likes != null ? likes.size() : 0;
    }

    public int getCommentCount() {
        return comments != null ? comments.size() : 0;
    }

    public String getTotalDuration() {
        if (playlistTracks == null || playlistTracks.isEmpty()) {
            return "0시간 0분";
        }
        int totalSeconds = playlistTracks.stream()
                .mapToInt(pt -> pt.getTrack().getDurationMs() / 1000)
                .sum();
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        return hours + "시간 " + minutes + "분";
    }
}

