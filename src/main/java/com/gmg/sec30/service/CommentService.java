package com.gmg.sec30.service;

import lombok.RequiredArgsConstructor;

import com.gmg.sec30.domain.Comment;

import com.gmg.sec30.dto.request.AddCommentRequest;
import com.gmg.sec30.dto.request.UpdateCommentRequest;

import com.gmg.sec30.repository.CommentRepository;
import com.gmg.sec30.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


/* 파일들이 import 되어야합니다. */

import com.gmg.sec30.domain.User;
import com.gmg.sec30.domain.Playlist;
import com.gmg.sec30.repository.PlaylistRepository;


/*
* 댓글을 총 관리하는 서비스입니다. 서비스에서 딱히 중요시봐야할 부분은 없겠습니다만,
* 간접적인 변수 형변환이 자주 일어나는 곳이라는 점은 알아두셔야할 것 같습니다.
* 예를 들어, save() 메서드를 보시면 playlistId는 playlist 엔티티의 기본 키이기 때문에, 자료형은 int형입니다만,
* comment 엔티티는 이를 참조해야하기에 강제로 playlist로 형이 강제됩니다. (Comment.java 엔티티 참조)
* 이를 해결하지 못할 경우 코드 복잡성이 증가할뿐더러, 무엇보다 그냥 작동이 안됩니다.
* 그렇기에, url을 통해 받아온 int형 id를 playlistRepository.findById라는 과정을 통해 playlist로 형변환 하는 과정이 필요합니다.
* 앞으로 서비스 함수에서 이런 형변환이 일어나게 되는 변수는 부모 댓글의 id인 parentId가 있겠습니다.
* */


@RequiredArgsConstructor
@Service
public class CommentService {

    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final CommentRepository commentRepository;

    /*
    *댓글 저장하는 함수입니다.
    * */

    public Comment save(Long playlistId, AddCommentRequest request, String userName) {
        Playlist playlist = playlistRepository.findById(playlistId).orElseThrow(() ->
                new IllegalArgumentException("댓글 쓰기 실패: 해당 플레이리스트가 존재하지 않습니다. " + playlistId));

        request.setAuthor(userName);
        request.setPlaylistId(playlist);

        return commentRepository.save(request.toEntity());
    }

    @Transactional(readOnly = true)
    public List<Comment> findAll(Long id) {
        Playlist playlist = playlistRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 플레이리스트가 존재하지 않습니다. id: " + id));
        List<Comment> comments = playlist.getComments();
        return comments;
    }

    @Transactional
    public Comment update(long id, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found :" + id));

        authorizeCommentAuthor(comment);
        comment.update(request.getComment(), request.getUpdatedAt());

        return comment;
    }

    public void delete(long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found :" + id));

        authorizeCommentAuthor(comment);
        commentRepository.delete(comment);
    }


    public Comment findById(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    public Comment saveReply(Long playlistId, Long parentId, AddCommentRequest request, String name) {

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Playlist not found"));

        Comment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));

        Comment reply = Comment.builder()
                .comment(request.getComment())
                .author(name)
                .parent(parentComment)
                .playlistId(playlist)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return commentRepository.save(reply);
    }

    /*
    * 교재에 있던 코드입니다.
    * 작동원리는, 로그인한 사람의 계정 정보를 getName()으로 불러와 userName에 저장 후,
    * 댓글을 단 사람의 정보와 불일치 하면 허가되지 않은 사람이라고 막는 기능입니다.
    * 작성은 몰라도 수정이나 삭제에는 권한이 필요하기에, 수정과 삭제 부분에만 들어가 있습니다.
    * */
    private static void authorizeCommentAuthor(Comment comment) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!comment.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }
}