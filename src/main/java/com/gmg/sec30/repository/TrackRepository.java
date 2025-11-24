package com.gmg.sec30.repository;

import com.gmg.sec30.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrackRepository extends JpaRepository<Track, String> {
    // trackId가 PK이므로 findById로 직접 검색 가능
}

