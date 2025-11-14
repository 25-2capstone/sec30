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

    @ManyToOne
    @JsonBackReference("parent-children")
    @JoinColumn(name = "parent_id", nullable = true)
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JsonManagedReference("parent-children")
    private List<Comment> children = new ArrayList<>();

    @ManyToOne
    @JsonBackReference("article-comments")
    @JoinColumn(name = "article_id", nullable = false)
    private Article articleId;

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