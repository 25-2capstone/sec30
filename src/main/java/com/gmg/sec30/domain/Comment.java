package com.gmg.sec30.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Entity

public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    /* JsonBackReference 어노테이션을 이용한 이유는, 이용하지 않을 시 무한 참조로 인해 작동이 되지 않았기 때문입니다. */
    @ManyToOne
    @JsonBackReference("parent-children")
    @JoinColumn(name = "parent_id", nullable = true)
    private Comment parent;

    /*
    * 자식 댓글들, 즉 답글 객체들을 전부 담게되는 객체입니다.
    * 이 객체들은 DB에 저장이되는것은 아닙니다. 각 자식 댓글이 자신의 parent_id 컬럼에 부모의 id만
    * 저장할 수 있게하는 연결 고리인 셈입니다.
    * 이 방식을 이용해서 comment-list.html에서 댓글과 답글을 구분해서 출력할 수 있게 합니다.
    * */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JsonManagedReference("parent-children")
    private List<Comment> children = new ArrayList<>();

    @ManyToOne
    @JsonBackReference("playlist-comments")
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlistId;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "author", nullable = false)
    private String author;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void update(String comment, LocalDateTime updatedAt) {
        this.comment = comment;
        this.updatedAt = updatedAt;
    }
}