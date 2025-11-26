package com.gmg.sec30.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE users SET delete_at = CURRENT_TIMESTAMP WHERE user_id = ?")
@Where(clause = "delete_at IS NULL")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "nickname", length = 20, nullable = false, unique = true)
    private String nickname;

    @Column(name = "name", length = 10, nullable = false)
    private String name;

    // --- 연관관계 매핑 ---

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Playlist> playlists = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites = new ArrayList<>();
}