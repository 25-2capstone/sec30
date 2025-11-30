package com.gmg.sec30.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Playlist 엔티티 테스트")
class PlaylistTest {

    @Test
    @DisplayName("Playlist 빌더로 생성 테스트")
    void createPlaylistWithBuilder() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("tester")
                .name("테스터")
                .build();

        String title = "내 플레이리스트";
        String description = "좋아하는 음악 모음";
        String imageUri = "https://example.com/image.jpg";

        // when
        Playlist playlist = Playlist.builder()
                .playlistTitle(title)
                .description(description)
                .imageUri(imageUri)
                .user(user)
                .build();

        // then
        assertThat(playlist.getPlaylistTitle()).isEqualTo(title);
        assertThat(playlist.getDescription()).isEqualTo(description);
        assertThat(playlist.getImageUri()).isEqualTo(imageUri);
        assertThat(playlist.getUser()).isEqualTo(user);
        assertThat(playlist.getComments()).isEmpty();
        assertThat(playlist.getFavorites()).isEmpty();
    }

    @Test
    @DisplayName("Playlist setter 테스트")
    void updatePlaylist() {
        // given
        Playlist playlist = new Playlist();
        String newTitle = "수정된 제목";
        String newDescription = "수정된 설명";

        // when
        playlist.setPlaylistTitle(newTitle);
        playlist.setDescription(newDescription);

        // then
        assertThat(playlist.getPlaylistTitle()).isEqualTo(newTitle);
        assertThat(playlist.getDescription()).isEqualTo(newDescription);
    }
}

