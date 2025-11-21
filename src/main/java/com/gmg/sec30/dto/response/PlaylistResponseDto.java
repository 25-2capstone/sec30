package com.gmg.sec30.dto.response;

import com.gmg.sec30.entity.Playlist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistResponseDto {
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private String username;
    private LocalDateTime createdAt;
    private int trackCount;
    private int likeCount;
    private int commentCount;
    private String totalDuration;
    private List<TrackResponseDto> tracks;

    public static PlaylistResponseDto from(Playlist playlist) {
        return PlaylistResponseDto.builder()
                .id(playlist.getId())
                .title(playlist.getTitle())
                .description(playlist.getDescription())
                .coverImage(playlist.getCoverImage())
                .username(playlist.getUser().getUsername())
                .createdAt(playlist.getCreatedAt())
                .trackCount(playlist.getTrackCount())
                .likeCount(playlist.getLikeCount())
                .commentCount(playlist.getCommentCount())
                .totalDuration(playlist.getTotalDuration())
                .tracks(playlist.getPlaylistTracks().stream()
                        .map(pt -> TrackResponseDto.from(pt.getTrack()))
                        .collect(Collectors.toList()))
                .build();
    }

    public static PlaylistResponseDto fromWithoutTracks(Playlist playlist) {
        return PlaylistResponseDto.builder()
                .id(playlist.getId())
                .title(playlist.getTitle())
                .description(playlist.getDescription())
                .coverImage(playlist.getCoverImage())
                .username(playlist.getUser().getUsername())
                .createdAt(playlist.getCreatedAt())
                .trackCount(playlist.getTrackCount())
                .likeCount(playlist.getLikeCount())
                .commentCount(playlist.getCommentCount())
                .totalDuration(playlist.getTotalDuration())
                .build();
    }
}

