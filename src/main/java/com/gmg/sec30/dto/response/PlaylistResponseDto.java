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
    private Integer id;
    private String title;
    private String description;
    private String coverImage;
    private String username;
    private LocalDateTime createdAt;
    private int trackCount;
    private int likeCount;
    private int commentCount;
    private List<TrackResponseDto> tracks;

    public static PlaylistResponseDto from(Playlist playlist) {
        return PlaylistResponseDto.builder()
                .id(playlist.getPlaylistId())
                .title(playlist.getPlaylistTitle())
                .description(playlist.getDescription())
                .coverImage(playlist.getImageUri())
                .username(playlist.getUser().getNickname())
                .createdAt(playlist.getCreateAt())
                .trackCount(playlist.getPlaylistTracks() != null ? playlist.getPlaylistTracks().size() : 0)
                .likeCount(playlist.getFavorites() != null ? playlist.getFavorites().size() : 0)
                .commentCount(playlist.getComments() != null ? playlist.getComments().size() : 0)
                .tracks(playlist.getPlaylistTracks().stream()
                        .map(pt -> TrackResponseDto.from(pt.getTrack()))
                        .collect(Collectors.toList()))
                .build();
    }

    public static PlaylistResponseDto fromWithoutTracks(Playlist playlist) {
        return PlaylistResponseDto.builder()
                .id(playlist.getPlaylistId())
                .title(playlist.getPlaylistTitle())
                .description(playlist.getDescription())
                .coverImage(playlist.getImageUri())
                .username(playlist.getUser().getNickname())
                .createdAt(playlist.getCreateAt())
                .trackCount(playlist.getPlaylistTracks() != null ? playlist.getPlaylistTracks().size() : 0)
                .likeCount(playlist.getFavorites() != null ? playlist.getFavorites().size() : 0)
                .commentCount(playlist.getComments() != null ? playlist.getComments().size() : 0)
                .build();
    }
}

