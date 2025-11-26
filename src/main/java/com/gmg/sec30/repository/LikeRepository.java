package com.gmg.sec30.repository;

import com.gmg.sec30.entity.Like;
import com.gmg.sec30.entity.Playlist;
import com.gmg.sec30.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPlaylist(User user, Playlist playlist);
    List<Like> findByUser(User user);
    List<Like> findByPlaylist(Playlist playlist);
    boolean existsByUserAndPlaylist(User user, Playlist playlist);
    long countByPlaylist(Playlist playlist);
}

