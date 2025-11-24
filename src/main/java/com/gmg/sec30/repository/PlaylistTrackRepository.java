package com.gmg.sec30.repository;

import com.gmg.sec30.entity.Playlist;
import com.gmg.sec30.entity.PlaylistTrack;
import com.gmg.sec30.entity.PlaylistTrackId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, PlaylistTrackId> {
    List<PlaylistTrack> findByPlaylistOrderByCreatedAtAsc(Playlist playlist);
    void deleteByPlaylistPlaylistId(Integer playlistId);
}

