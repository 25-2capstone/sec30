package com.gmg.sec30.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class YouTubeService {

    private static final Logger log = LoggerFactory.getLogger(YouTubeService.class);

    @Value("${YOUTUBE_API_KEY:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 트랙명과 아티스트명으로 YouTube 비디오 ID 검색
     */
    public String searchVideoId(String trackName, String artistName) {
        log.info("🔍 YouTube search requested for: {} - {}", trackName, artistName);
        log.info("📌 API Key status: {}", (apiKey == null || apiKey.isEmpty()) ? "NOT SET" :
                 apiKey.contains("Dummy") ? "DUMMY VALUE" : "CONFIGURED (length: " + apiKey.length() + ")");

        if (apiKey == null || apiKey.isEmpty() || apiKey.contains("Dummy")) {
            log.warn("⚠️ YouTube API key not configured - skipping YouTube search");
            return null;
        }

        try {
            String query = trackName + " " + artistName + " official audio";
            String url = String.format(
                "https://www.googleapis.com/youtube/v3/search?part=snippet&q=%s&type=video&videoCategoryId=10&maxResults=1&key=%s",
                java.net.URLEncoder.encode(query, "UTF-8"),
                apiKey
            );

            log.info("🌐 Calling YouTube API for: {}", query);

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.get("items");

            if (items != null && items.isArray() && !items.isEmpty()) {
                String videoId = items.get(0).get("id").get("videoId").asText();
                log.info("✅ Found YouTube video: {} for track: {}", videoId, trackName);
                return videoId;
            }

            log.warn("❌ No YouTube video found for: {}", query);
            return null;

        } catch (Exception e) {
            log.error("💥 YouTube API error for {} - {}: {}", trackName, artistName, e.getMessage(), e);
            return null;
        }
    }
}

