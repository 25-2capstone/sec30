package com.gmg.sec30.repository;

import com.gmg.sec30.entity.Playlist;
import com.gmg.sec30.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("PlaylistRepository 테스트")
class PlaylistRepositoryTest {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("playlist@example.com")
                .password("password")
                .nickname("playlistMaker")
                .name("플레이리스트메이커")
                .build();
        testUser = userRepository.save(testUser);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("플레이리스트 저장 및 조회 테스트")
    void saveAndFindPlaylist() {
        // given
        Playlist playlist = Playlist.builder()
                .playlistTitle("내 플레이리스트")
                .description("좋아하는 음악들")
                .imageUri("https://example.com/image.jpg")
                .user(testUser)
                .build();

        // when
        Playlist savedPlaylist = playlistRepository.save(playlist);
        entityManager.flush();
        entityManager.clear();

        // then
        Optional<Playlist> foundPlaylist = playlistRepository.findById(savedPlaylist.getPlaylistId());
        assertThat(foundPlaylist).isPresent();
        assertThat(foundPlaylist.get().getPlaylistTitle()).isEqualTo("내 플레이리스트");
        assertThat(foundPlaylist.get().getDescription()).isEqualTo("좋아하는 음악들");
    }

    @Test
    @DisplayName("사용자의 모든 플레이리스트 조회 테스트")
    void findByUser() {
        // given
        Playlist playlist1 = Playlist.builder()
                .playlistTitle("플레이리스트1")
                .user(testUser)
                .build();
        Playlist playlist2 = Playlist.builder()
                .playlistTitle("플레이리스트2")
                .user(testUser)
                .build();
        playlistRepository.save(playlist1);
        playlistRepository.save(playlist2);
        entityManager.flush();
        entityManager.clear();

        // when
        List<Playlist> playlists = playlistRepository.findByUserOrderByCreateAtDesc(testUser);

        // then
        assertThat(playlists).hasSize(2);
        assertThat(playlists).extracting(Playlist::getPlaylistTitle)
                .containsExactlyInAnyOrder("플레이리스트1", "플레이리스트2");
    }

    @Test
    @DisplayName("플레이리스트 삭제 테스트 (Soft Delete)")
    void deletePlaylist() {
        // given
        Playlist playlist = Playlist.builder()
                .playlistTitle("삭제될 플레이리스트")
                .user(testUser)
                .build();
        Playlist savedPlaylist = playlistRepository.save(playlist);
        entityManager.flush();
        entityManager.clear();

        // when
        playlistRepository.delete(savedPlaylist);
        entityManager.flush();
        entityManager.clear();

        // then
        Optional<Playlist> deletedPlaylist = playlistRepository.findById(savedPlaylist.getPlaylistId());
        assertThat(deletedPlaylist).isEmpty(); // Soft delete로 인해 조회되지 않음
    }
}

