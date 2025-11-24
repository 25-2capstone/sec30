package com.gmg.sec30.repository;

import com.gmg.sec30.entity.Favorite;
import com.gmg.sec30.entity.FavoriteId;
import com.gmg.sec30.entity.Playlist;
import com.gmg.sec30.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    Optional<Favorite> findByUserAndPlaylist(User user, Playlist playlist);
    boolean existsByUserAndPlaylist(User user, Playlist playlist);
    long countByPlaylist(Playlist playlist);
}

