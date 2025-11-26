package com.gmg.sec30.repository;

import com.gmg.sec30.entity.Playlist;
import com.gmg.sec30.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    List<Playlist> findByUserOrderByCreateAtDesc(User user);
    Page<Playlist> findByUserOrderByCreateAtDesc(User user, Pageable pageable);

    @Query("SELECT p FROM Playlist p WHERE p.playlistTitle LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Playlist> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM Playlist p ORDER BY SIZE(p.favorites) DESC")
    List<Playlist> findPopularPlaylists(Pageable pageable);
}

