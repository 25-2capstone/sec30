package com.gmg.sec30.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistTrackId implements Serializable {
    private Integer playlist; // Playlist 엔티티의 @Id 필드명(playlist)과 일치
    private String track;     // Track 엔티티의 @Id 필드명(track)과 일치
}