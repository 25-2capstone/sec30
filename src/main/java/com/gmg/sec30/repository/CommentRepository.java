package com.gmg.sec30.repository;

import com.gmg.sec30.entity.Comment;
import com.gmg.sec30.entity.Playlist;
import com.gmg.sec30.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPlaylistOrderByCreatedAtDesc(Playlist playlist);
    List<Comment> findByUserOrderByCreatedAtDesc(User user);
}

