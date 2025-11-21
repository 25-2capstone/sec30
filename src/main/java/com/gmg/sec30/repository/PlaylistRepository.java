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
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUserOrderByCreatedAtDesc(User user);
    Page<Playlist> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    @Query("SELECT p FROM Playlist p WHERE p.title LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Playlist> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM Playlist p ORDER BY SIZE(p.likes) DESC")
    List<Playlist> findPopularPlaylists(Pageable pageable);
}

