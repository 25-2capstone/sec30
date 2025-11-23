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
        if (tracks.isEmpty()) {
            tracks = demoTracks();
        }
        model.addAttribute("tracks", tracks);

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
        if (tracks.isEmpty()) {
            tracks = demoTracks();
        }
        model.addAttribute("tracks", tracks);
        model.addAttribute("trackCount", tracks.size());
        return "tracks";
    }

    private List<Track> demoTracks() {
        List<Track> list = new ArrayList<>();
        list.add(Track.builder()
                .spotifyId("demo1")
                .name("Demo Track 1")
                .artist("Demo Artist")
                .album("Demo Album")
                .albumImage("https://via.placeholder.com/300x300?text=Demo1")
                .durationMs(180000)
                .previewUrl(null)
                .build());
        list.add(Track.builder()
                .spotifyId("demo2")
                .name("Demo Track 2")
                .artist("Demo Artist")
                .album("Demo Album")
                .albumImage("https://via.placeholder.com/300x300?text=Demo2")
                .durationMs(200000)
                .previewUrl(null)
                .build());
        return list;
    }
}
