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
    private String trackId;
    private String spotifyId; // 호환성을 위해 유지
    private String name;
    private String artist;
    private String album;
    private String albumImage;
    private String previewUrl;

    public static TrackResponseDto from(Track track) {
        return TrackResponseDto.builder()
                .trackId(track.getTrackId())
                .spotifyId(track.getTrackId()) // trackId를 spotifyId로도 반환
                .name(track.getTrackTitle())
                .artist(track.getArtistName())
                .album(track.getAlbumName())
                .albumImage(track.getImageUri())
                .previewUrl(track.getPreviewUrl())
                .build();
    }
}

