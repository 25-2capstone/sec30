package com.gmg.sec30.controller;

import com.gmg.sec30.dto.response.PlaylistResponseDto;
import com.gmg.sec30.entity.Track;
import com.gmg.sec30.service.PlaylistService;
import com.gmg.sec30.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class MusicController {

    private final PlaylistService playlistService;
    private final SpotifyService spotifyService;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("title", "홈");

        // 인기 트랙 가져오기
        List<Track> tracks = spotifyService.getPopularTracks(12);

        // 빈 결과면 데모 사용
        if (tracks.isEmpty()) {
            tracks = demoTracks();
        }

        // JavaScript에서 사용할 수 있도록 Map으로 변환
        List<java.util.Map<String, Object>> tracksForJs = tracks.stream()
                .map(t -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("trackId", t.getTrackId());
                    map.put("name", t.getTrackTitle());
                    map.put("artist", t.getArtistName());
                    map.put("album", t.getAlbumName());
                    map.put("albumImage", t.getImageUri());
                    map.put("imageUri", t.getImageUri());
                    map.put("previewUrl", t.getPreviewUrl());
                    map.put("spotifyId", t.getTrackId());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());


        model.addAttribute("tracks", tracks);
        model.addAttribute("tracksJson", tracksForJs);

        // 믹스 정보 (추후 구현 가능)
        // List<Mix> mixes = musicService.getRecommendedMixes();
        // model.addAttribute("mixes", mixes);

        return "home/index";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        model.addAttribute("title", "로그인");
        return "login";
    }

    @GetMapping("/tracks")
    public String tracks(@RequestParam(value = "query", required = false) String query, Model model) {
        List<PlaylistResponseDto> popularPlaylists = playlistService.getPopularPlaylists(PageRequest.of(0, 10));
        model.addAttribute("popularPlaylists", popularPlaylists);
        model.addAttribute("searchQuery", query);

        List<Track> tracks;
        if (query != null && !query.isBlank()) {
            tracks = spotifyService.searchTracks(query, 20);
        } else {
            tracks = spotifyService.getPopularTracks(20);
        }

        // 빈 결과면 데모 사용
        if (tracks.isEmpty()) {
            tracks = demoTracks();
        }

        // JavaScript에서 사용할 수 있도록 Map으로 변환
        List<java.util.Map<String, Object>> tracksForJs = tracks.stream()
                .map(t -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("trackId", t.getTrackId());
                    map.put("name", t.getTrackTitle());
                    map.put("artist", t.getArtistName());
                    map.put("album", t.getAlbumName());
                    map.put("albumImage", t.getImageUri());
                    map.put("imageUri", t.getImageUri());
                    map.put("previewUrl", t.getPreviewUrl());
                    map.put("spotifyId", t.getTrackId());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("tracks", tracks);
        model.addAttribute("tracksJson", tracksForJs);
        model.addAttribute("trackCount", tracks.size());
        return "tracks";
    }

    private List<Track> demoTracks() {
        List<Track> list = new ArrayList<>();
        // 재생 가능한 샘플 트랙 (실제 무료 음원)
        list.add(Track.builder()
                .trackId("demo1")
                .trackTitle("Chill Vibes")
                .artistName("Indie Artist")
                .albumName("Relaxing Beats")
                .imageUri("https://picsum.photos/seed/music1/300/300")
                .previewUrl("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
                .build());
        list.add(Track.builder()
                .trackId("demo2")
                .trackTitle("Summer Dreams")
                .artistName("Electronic Sounds")
                .albumName("Feel Good Collection")
                .imageUri("https://picsum.photos/seed/music2/300/300")
                .previewUrl("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3")
                .build());
        list.add(Track.builder()
                .trackId("demo3")
                .trackTitle("Night Drive")
                .artistName("Synth Wave")
                .albumName("Midnight Sessions")
                .imageUri("https://picsum.photos/seed/music3/300/300")
                .previewUrl("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3")
                .build());
        list.add(Track.builder()
                .trackId("demo4")
                .trackTitle("Ocean Waves")
                .artistName("Ambient Music")
                .albumName("Nature Sounds")
                .imageUri("https://picsum.photos/seed/music4/300/300")
                .previewUrl("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3")
                .build());
        list.add(Track.builder()
                .trackId("demo5")
                .trackTitle("Urban Jungle")
                .artistName("Hip Hop Beats")
                .albumName("City Life")
                .imageUri("https://picsum.photos/seed/music5/300/300")
                .previewUrl("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3")
                .build());
        list.add(Track.builder()
                .trackId("demo6")
                .trackTitle("Morning Coffee")
                .artistName("Jazz Ensemble")
                .albumName("Smooth Jazz")
                .imageUri("https://picsum.photos/seed/music6/300/300")
                .previewUrl("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3")
                .build());
        return list;
    }
}
