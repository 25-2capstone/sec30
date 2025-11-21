package com.gmg.sec30.dto.response;

import com.gmg.sec30.entity.Track;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackResponseDto {
    private Long id;
    private String spotifyId;
    private String name;
    private String artist;
    private String album;
    private String albumImage;
    private Integer durationMs;
    private String formattedDuration;
    private String previewUrl;

    public static TrackResponseDto from(Track track) {
        return TrackResponseDto.builder()
                .id(track.getId())
                .spotifyId(track.getSpotifyId())
                .name(track.getName())
                .artist(track.getArtist())
                .album(track.getAlbum())
                .albumImage(track.getAlbumImage())
                .durationMs(track.getDurationMs())
                .formattedDuration(track.getFormattedDuration())
                .previewUrl(track.getPreviewUrl())
                .build();
    }
}

