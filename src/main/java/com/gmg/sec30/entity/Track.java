package com.gmg.sec30.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tracks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String spotifyId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String artist;

    private String album;

    private String albumImage;

    private Integer durationMs;

    private String previewUrl;

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PlaylistTrack> playlistTracks = new ArrayList<>();

    public String getFormattedDuration() {
        if (durationMs == null) return "0:00";
        int totalSeconds = durationMs / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}

