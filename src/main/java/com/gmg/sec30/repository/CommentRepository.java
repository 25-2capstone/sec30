package com.gmg.sec30.repository;

import com.gmg.sec30.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.playlistId.id = :playlistId AND c.parent IS NULL ORDER BY c.createdAt DESC")
    List<Comment> findByPlaylistIdAndParentIsNull(@Param("playlistId") Long playlistId);
}