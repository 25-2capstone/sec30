package com.gmg.sec30.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteId implements Serializable {
    private Integer user; // User 엔티티의 @Id 필드명(user)과 일치해야 함
    private Integer playlist; // Playlist 엔티티의 @Id 필드명(playlist)과 일치해야 함
}