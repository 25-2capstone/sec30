package com.gmg.sec30.service;

import com.gmg.sec30.dto.request.PlaylistRequestDto;
import com.gmg.sec30.dto.request.TrackRequestDto;
import com.gmg.sec30.dto.response.PlaylistResponseDto;
import com.gmg.sec30.entity.Playlist;
import com.gmg.sec30.entity.PlaylistTrack;
import com.gmg.sec30.entity.Track;
import com.gmg.sec30.entity.User;
import com.gmg.sec30.repository.PlaylistRepository;
import com.gmg.sec30.repository.PlaylistTrackRepository;
import com.gmg.sec30.repository.TrackRepository;
import com.gmg.sec30.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;

    @Transactional
    public PlaylistResponseDto createPlaylist(String username, PlaylistRequestDto dto) {
        System.out.println("[PlaylistService] createPlaylist 시작");
        System.out.println("[PlaylistService] username: " + username);
        System.out.println("[PlaylistService] dto: " + dto);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.err.println("[PlaylistService] 사용자를 찾을 수 없음: " + username);
                    return new RuntimeException("User not found: " + username);
                });

        System.out.println("[PlaylistService] 사용자 찾음: " + user.getUsername() + " (ID: " + user.getId() + ")");

        Playlist playlist = Playlist.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .coverImage(dto.getCoverImage())
                .user(user)
                .build();

        System.out.println("[PlaylistService] Playlist 엔티티 생성 완료");

        Playlist savedPlaylist = playlistRepository.save(playlist);
        System.out.println("[PlaylistService] Playlist 저장 완료! ID: " + savedPlaylist.getId());

        return PlaylistResponseDto.from(savedPlaylist);
    }

    @Transactional
    public PlaylistResponseDto updatePlaylist(Long id, String username, PlaylistRequestDto dto) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        if (!playlist.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        playlist.setTitle(dto.getTitle());
        playlist.setDescription(dto.getDescription());
        playlist.setCoverImage(dto.getCoverImage());

        return PlaylistResponseDto.from(playlist);
    }

    @Transactional
    public void deletePlaylist(Long id, String username) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        if (!playlist.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        playlistRepository.delete(playlist);
    }

    public PlaylistResponseDto getPlaylist(Long id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        return PlaylistResponseDto.from(playlist);
    }

    public List<PlaylistResponseDto> getUserPlaylists(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return playlistRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(PlaylistResponseDto::fromWithoutTracks)
                .collect(Collectors.toList());
    }

    public Page<PlaylistResponseDto> getUserPlaylistsPaged(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return playlistRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(PlaylistResponseDto::fromWithoutTracks);
    }

    public List<PlaylistResponseDto> getPopularPlaylists(Pageable pageable) {
        return playlistRepository.findPopularPlaylists(pageable).stream()
                .map(PlaylistResponseDto::fromWithoutTracks)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addTrackToPlaylist(Long playlistId, String username, TrackRequestDto dto) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        if (!playlist.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        Track track = trackRepository.findBySpotifyId(dto.getSpotifyId())
                .orElseGet(() -> {
                    Track newTrack = Track.builder()
                            .spotifyId(dto.getSpotifyId())
                            .name(dto.getName())
                            .artist(dto.getArtist())
                            .album(dto.getAlbum())
                            .albumImage(dto.getAlbumImage())
                            .durationMs(dto.getDurationMs())
                            .previewUrl(dto.getPreviewUrl())
                            .build();
                    return trackRepository.save(newTrack);
                });

        int position = playlist.getPlaylistTracks().size();
        PlaylistTrack playlistTrack = PlaylistTrack.builder()
                .playlist(playlist)
                .track(track)
                .position(position)
                .build();

        playlistTrackRepository.save(playlistTrack);
    }

    @Transactional
    public void removeTrackFromPlaylist(Long playlistId, Long trackId, String username) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        if (!playlist.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        playlist.getPlaylistTracks().removeIf(pt -> pt.getTrack().getId().equals(trackId));
    }
}

