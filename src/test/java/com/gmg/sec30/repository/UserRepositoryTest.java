package com.gmg.sec30.repository;

import com.gmg.sec30.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("UserRepository 테스트")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("사용자 저장 및 조회 테스트")
    void saveAndFindUser() {
        // given
        User user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("tester")
                .name("테스터")
                .build();

        // when
        User savedUser = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        // then
        Optional<User> foundUser = userRepository.findById(savedUser.getUserId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@example.com");
        assertThat(foundUser.get().getNickname()).isEqualTo("tester");
    }

    @Test
    @DisplayName("이메일로 사용자 조회 테스트")
    void findByEmail() {
        // given
        User user = User.builder()
                .email("find@example.com")
                .password("password")
                .nickname("finder")
                .name("파인더")
                .build();
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        // when
        Optional<User> foundUser = userRepository.findByEmail("find@example.com");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("find@example.com");
        assertThat(foundUser.get().getNickname()).isEqualTo("finder");
    }

    @Test
    @DisplayName("닉네임으로 사용자 조회 테스트")
    void findByNickname() {
        // given
        User user = User.builder()
                .email("nick@example.com")
                .password("password")
                .nickname("nicknamer")
                .name("닉네이머")
                .build();
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        // when
        Optional<User> foundUser = userRepository.findByNickname("nicknamer");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getNickname()).isEqualTo("nicknamer");
        assertThat(foundUser.get().getEmail()).isEqualTo("nick@example.com");
    }

    @Test
    @DisplayName("존재하지 않는 이메일 조회 시 empty 반환")
    void findByEmailNotFound() {
        // when
        Optional<User> foundUser = userRepository.findByEmail("notfound@example.com");

        // then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("사용자 삭제 테스트 (Soft Delete)")
    void deleteUser() {
        // given
        User user = User.builder()
                .email("delete@example.com")
                .password("password")
                .nickname("deleter")
                .name("삭제자")
                .build();
        User savedUser = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        // when
        userRepository.delete(savedUser);
        entityManager.flush();
        entityManager.clear();

        // then
        Optional<User> deletedUser = userRepository.findById(savedUser.getUserId());
        assertThat(deletedUser).isEmpty(); // Soft delete로 인해 조회되지 않음
    }
}

