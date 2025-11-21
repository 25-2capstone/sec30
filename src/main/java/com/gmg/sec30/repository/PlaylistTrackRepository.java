package com.gmg.sec30.repository;

import com.gmg.sec30.entity.Playlist;
import com.gmg.sec30.entity.PlaylistTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, Long> {
    List<PlaylistTrack> findByPlaylistOrderByPositionAsc(Playlist playlist);
    void deleteByPlaylistId(Long playlistId);
}

