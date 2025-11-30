package com.gmg.sec30.service;

import com.gmg.sec30.dto.request.PlaylistRequestDto;
import com.gmg.sec30.dto.response.PlaylistResponseDto;
import com.gmg.sec30.entity.Playlist;
import com.gmg.sec30.entity.User;
import com.gmg.sec30.repository.PlaylistRepository;
import com.gmg.sec30.repository.PlaylistTrackRepository;
import com.gmg.sec30.repository.TrackRepository;
import com.gmg.sec30.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlaylistService 테스트")
class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private PlaylistTrackRepository playlistTrackRepository;

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PlaylistService playlistService;

    @Test
    @DisplayName("플레이리스트 생성 성공")
    void createPlaylist_Success() {
        // given
        String username = "tester";
        User user = User.builder()
                .userId(1)
                .nickname(username)
                .email("test@example.com")
                .build();

        PlaylistRequestDto requestDto = new PlaylistRequestDto();
        requestDto.setTitle("내 플레이리스트");
        requestDto.setDescription("좋아하는 음악");
        requestDto.setCoverImage("https://example.com/image.jpg");

        Playlist savedPlaylist = Playlist.builder()
                .playlistId(1)
                .playlistTitle(requestDto.getTitle())
                .description(requestDto.getDescription())
                .imageUri(requestDto.getCoverImage())
                .user(user)
                .build();

        given(userRepository.findByNickname(username)).willReturn(Optional.of(user));
        given(playlistRepository.save(any(Playlist.class))).willReturn(savedPlaylist);

        // when
        PlaylistResponseDto result = playlistService.createPlaylist(username, requestDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(result.getDescription()).isEqualTo(requestDto.getDescription());

        verify(userRepository).findByNickname(username);
        verify(playlistRepository).save(any(Playlist.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 플레이리스트 생성 실패")
    void createPlaylist_UserNotFound() {
        // given
        String username = "notfound";
        PlaylistRequestDto requestDto = new PlaylistRequestDto();
        requestDto.setTitle("플레이리스트");

        given(userRepository.findByNickname(username)).willReturn(Optional.empty());
        given(userRepository.findByEmail(username)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> playlistService.createPlaylist(username, requestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("플레이리스트 조회 성공")
    void getPlaylist_Success() {
        // given
        Integer playlistId = 1;
        User user = User.builder()
                .userId(1)
                .nickname("owner")
                .build();

        Playlist playlist = Playlist.builder()
                .playlistId(playlistId)
                .playlistTitle("테스트 플레이리스트")
                .description("설명")
                .user(user)
                .build();

        given(playlistRepository.findById(playlistId)).willReturn(Optional.of(playlist));

        // when
        PlaylistResponseDto result = playlistService.getPlaylist(playlistId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("테스트 플레이리스트");
        verify(playlistRepository).findById(playlistId);
    }

    @Test
    @DisplayName("존재하지 않는 플레이리스트 조회 실패")
    void getPlaylist_NotFound() {
        // given
        Integer playlistId = 999;
        given(playlistRepository.findById(playlistId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> playlistService.getPlaylist(playlistId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Playlist not found");
    }

    @Test
    @DisplayName("사용자의 플레이리스트 목록 조회")
    void getUserPlaylists_Success() {
        // given
        String username = "tester";
        User user = User.builder()
                .userId(1)
                .nickname(username)
                .build();

        Playlist playlist1 = Playlist.builder()
                .playlistId(1)
                .playlistTitle("플레이리스트1")
                .user(user)
                .build();

        Playlist playlist2 = Playlist.builder()
                .playlistId(2)
                .playlistTitle("플레이리스트2")
                .user(user)
                .build();

        given(userRepository.findByNickname(username)).willReturn(Optional.of(user));
        given(playlistRepository.findByUserOrderByCreateAtDesc(user)).willReturn(List.of(playlist1, playlist2));

        // when
        List<PlaylistResponseDto> result = playlistService.getUserPlaylists(username);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(PlaylistResponseDto::getTitle)
                .containsExactlyInAnyOrder("플레이리스트1", "플레이리스트2");
    }

    @Test
    @DisplayName("플레이리스트 삭제 성공")
    void deletePlaylist_Success() {
        // given
        Integer playlistId = 1;
        String username = "owner";
        User user = User.builder()
                .userId(1)
                .nickname(username)
                .build();

        Playlist playlist = Playlist.builder()
                .playlistId(playlistId)
                .playlistTitle("삭제할 플레이리스트")
                .user(user)
                .build();

        given(playlistRepository.findById(playlistId)).willReturn(Optional.of(playlist));

        // when
        playlistService.deletePlaylist(playlistId, username);

        // then
        verify(playlistRepository).findById(playlistId);
        verify(playlistRepository).delete(playlist);
    }

    @Test
    @DisplayName("권한 없는 사용자의 플레이리스트 삭제 실패")
    void deletePlaylist_Unauthorized() {
        // given
        Integer playlistId = 1;
        String owner = "owner";
        String attacker = "attacker";

        User user = User.builder()
                .userId(1)
                .nickname(owner)
                .build();

        Playlist playlist = Playlist.builder()
                .playlistId(playlistId)
                .user(user)
                .build();

        given(playlistRepository.findById(playlistId)).willReturn(Optional.of(playlist));

        // when & then
        assertThatThrownBy(() -> playlistService.deletePlaylist(playlistId, attacker))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unauthorized");
    }
}

