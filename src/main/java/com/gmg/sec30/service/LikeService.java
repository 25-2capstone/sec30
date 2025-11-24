package com.gmg.sec30.service;

import com.gmg.sec30.entity.Favorite;
import com.gmg.sec30.entity.Playlist;
import com.gmg.sec30.entity.User;
import com.gmg.sec30.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final FavoriteRepository favoriteRepository;
    private final DomainLookupService lookup;

    @Transactional
    public void toggleLike(Integer playlistId, String username) {
        User user = lookup.requireUser(username);
        Playlist playlist = lookup.requirePlaylist(playlistId);

        favoriteRepository.findByUserAndPlaylist(user, playlist)
                .ifPresentOrElse(
                        favoriteRepository::delete,
                        () -> {
                            Favorite favorite = Favorite.builder()
                                    .user(user)
                                    .playlist(playlist)
                                    .build();
                            favoriteRepository.save(favorite);
                        }
                );
    }

    public boolean isLiked(Integer playlistId, String username) {
        User user = lookup.requireUser(username);
        Playlist playlist = lookup.requirePlaylist(playlistId);
        return favoriteRepository.existsByUserAndPlaylist(user, playlist);
    }

    public long getLikeCount(Integer playlistId) {
        Playlist playlist = lookup.requirePlaylist(playlistId);
        return favoriteRepository.countByPlaylist(playlist);
    }
}
