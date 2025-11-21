package com.gmg.sec30.service;

import com.gmg.sec30.entity.Like;
import com.gmg.sec30.entity.Playlist;
import com.gmg.sec30.entity.User;
import com.gmg.sec30.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;
    private final DomainLookupService lookup;

    @Transactional
    public void toggleLike(Long playlistId, String username) {
        User user = lookup.requireUser(username);
        Playlist playlist = lookup.requirePlaylist(playlistId);

        likeRepository.findByUserAndPlaylist(user, playlist)
                .ifPresentOrElse(
                        likeRepository::delete,
                        () -> {
                            Like like = Like.builder()
                                    .user(user)
                                    .playlist(playlist)
                                    .build();
                            likeRepository.save(like);
                        }
                );
    }

    public boolean isLiked(Long playlistId, String username) {
        User user = lookup.requireUser(username);
        Playlist playlist = lookup.requirePlaylist(playlistId);
        return likeRepository.existsByUserAndPlaylist(user, playlist);
    }

    public long getLikeCount(Long playlistId) {
        Playlist playlist = lookup.requirePlaylist(playlistId);
        return likeRepository.countByPlaylist(playlist);
    }
}
