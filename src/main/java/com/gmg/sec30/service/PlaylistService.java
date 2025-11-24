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

        User user = userRepository.findByNickname(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> {
                    System.err.println("[PlaylistService] 사용자를 찾을 수 없음: " + username);
                    return new RuntimeException("User not found: " + username);
                });

        System.out.println("[PlaylistService] 사용자 찾음: " + user.getNickname() + " (ID: " + user.getUserId() + ")");

        Playlist playlist = Playlist.builder()
                .playlistTitle(dto.getTitle())
                .description(dto.getDescription())
                .imageUri(dto.getCoverImage())
                .user(user)
                .build();

        System.out.println("[PlaylistService] Playlist 엔티티 생성 완료");

        Playlist savedPlaylist = playlistRepository.save(playlist);
        System.out.println("[PlaylistService] Playlist 저장 완료! ID: " + savedPlaylist.getPlaylistId());

        return PlaylistResponseDto.from(savedPlaylist);
    }

    @Transactional
    public PlaylistResponseDto updatePlaylist(Integer id, String username, PlaylistRequestDto dto) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        if (!playlist.getUser().getNickname().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        playlist.setPlaylistTitle(dto.getTitle());
        playlist.setDescription(dto.getDescription());
        playlist.setImageUri(dto.getCoverImage());

        return PlaylistResponseDto.from(playlist);
    }

    @Transactional
    public void deletePlaylist(Integer id, String username) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        if (!playlist.getUser().getNickname().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        playlistRepository.delete(playlist);
    }

    public PlaylistResponseDto getPlaylist(Integer id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        return PlaylistResponseDto.from(playlist);
    }

    public List<PlaylistResponseDto> getUserPlaylists(String username) {
        User user = userRepository.findByNickname(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new RuntimeException("User not found"));

        return playlistRepository.findByUserOrderByCreateAtDesc(user).stream()
                .map(PlaylistResponseDto::fromWithoutTracks)
                .collect(Collectors.toList());
    }

    public Page<PlaylistResponseDto> getUserPlaylistsPaged(String username, Pageable pageable) {
        User user = userRepository.findByNickname(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new RuntimeException("User not found"));

        return playlistRepository.findByUserOrderByCreateAtDesc(user, pageable)
                .map(PlaylistResponseDto::fromWithoutTracks);
    }

    public List<PlaylistResponseDto> getPopularPlaylists(Pageable pageable) {
        return playlistRepository.findPopularPlaylists(pageable).stream()
                .map(PlaylistResponseDto::fromWithoutTracks)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addTrackToPlaylist(Integer playlistId, String username, TrackRequestDto dto) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        if (!playlist.getUser().getNickname().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        Track track = trackRepository.findById(dto.getSpotifyId())
                .orElseGet(() -> {
                    Track newTrack = Track.builder()
                            .trackId(dto.getSpotifyId())
                            .trackTitle(dto.getName())
                            .artistName(dto.getArtist())
                            .albumName(dto.getAlbum())
                            .imageUri(dto.getAlbumImage())
                            .previewUrl(dto.getPreviewUrl())
                            .build();
                    return trackRepository.save(newTrack);
                });

        PlaylistTrack playlistTrack = PlaylistTrack.builder()
                .playlist(playlist)
                .track(track)
                .build();

        playlistTrackRepository.save(playlistTrack);
    }

    @Transactional
    public void removeTrackFromPlaylist(Integer playlistId, String trackId, String username) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        if (!playlist.getUser().getNickname().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        playlist.getPlaylistTracks().removeIf(pt -> pt.getTrack().getTrackId().equals(trackId));
    }
}

