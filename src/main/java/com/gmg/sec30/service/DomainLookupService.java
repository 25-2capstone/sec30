package com.gmg.sec30.service;

import com.gmg.sec30.entity.Playlist;
import com.gmg.sec30.entity.User;
import com.gmg.sec30.repository.PlaylistRepository;
import com.gmg.sec30.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DomainLookupService {

    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;

    public User requireUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Playlist requirePlaylist(Long id) {
        return playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
    }
}

