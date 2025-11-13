package com.gmg.sec30.controller;

import com.gmg.sec30.entity.Track;
import com.gmg.sec30.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 음악 관련 요청을 처리하는 컨트롤러
 * 메인 페이지, 트랙 목록, 검색 등의 기능을 제공합니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class MusicController {

    private final SpotifyService spotifyService;

    /**
     * 메인 페이지 (루트 경로)
     * 인기 트랙 40개를 표시합니다.
     *
     * @param model 뷰에 전달할 모델
     * @return tracks 템플릿
     */
    @GetMapping("/")
    public String mainPage(Model model) {
        log.info("메인 페이지 요청");
        return getTracks(null, model);
    }

    /**
     * 트랙 목록 페이지
     * 검색어가 있으면 검색 결과를, 없으면 인기 트랙을 표시합니다.
     *
     * @param query 검색어 (선택사항)
     * @param model 뷰에 전달할 모델
     * @return tracks 템플릿
     */
    @GetMapping("/tracks")
    public String getTracks(@RequestParam(required = false) String query, Model model) {
        try {
            List<Track> tracks;

            if (query != null && !query.trim().isEmpty()) {
                log.info("트랙 검색 요청: query={}", query);
                tracks = spotifyService.searchTracks(query, 40);
                model.addAttribute("searchQuery", query);
            } else {
                log.info("인기 트랙 조회 요청");
                tracks = spotifyService.getPopularTracks(40);
            }

            model.addAttribute("tracks", tracks);
            model.addAttribute("trackCount", tracks.size());

            log.info("트랙 {} 개 조회 완료", tracks.size());

        } catch (Exception e) {
            log.error("트랙 조회 중 오류 발생", e);
            model.addAttribute("error", "트랙을 불러오는 중 오류가 발생했습니다.");
            model.addAttribute("tracks", List.of());
        }

        return "tracks";
    }

    /**
     * 404 에러 페이지로 리다이렉트
     *
     * @param model 뷰에 전달할 모델
     * @return notfound 템플릿
     */
    @GetMapping("/notfound")
    public String notFound(Model model) {
        model.addAttribute("message", "요청하신 페이지를 찾을 수 없습니다.");
        model.addAttribute("redirectTo", "/tracks");
        model.addAttribute("delaySec", 3);
        return "notfound";
    }
}

