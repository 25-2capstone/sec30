package com.gmg.sec30.service;

import com.gmg.sec30.entity.Track;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Spotify API와 통신하는 서비스
 * 인기 트랙 조회, 검색 등의 기능을 제공합니다.
 */
@Slf4j
@Service
public class SpotifyService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private String accessToken;
    private long tokenExpiresAt = 0;

    /**
     * Spotify Access Token을 가져옵니다.
     * 토큰이 만료되었거나 없으면 새로 발급받습니다.
     *
     * @return access token
     * @throws IOException 네트워크 오류
     * @throws InterruptedException 요청 중단
     */
    private String getAccessToken() throws IOException, InterruptedException {
        if (accessToken != null && System.currentTimeMillis() < tokenExpiresAt) {
            return accessToken;
        }

        log.info("Requesting new Spotify access token");

        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .header("Authorization", "Basic " + encodedAuth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            accessToken = json.get("access_token").getAsString();
            int expiresIn = json.get("expires_in").getAsInt();
            tokenExpiresAt = System.currentTimeMillis() + (expiresIn - 60) * 1000L;
            log.info("Access token obtained successfully");
            return accessToken;
        } else {
            log.error("Failed to get access token: {} - {}", response.statusCode(), response.body());
            throw new RuntimeException("Failed to get Spotify access token: " + response.statusCode());
        }
    }

    /**
     * 인기 있는 트랙을 가져옵니다.
     *
     * @param limit 가져올 트랙 수 (최대 50)
     * @return 트랙 리스트
     */
    public List<Track> getPopularTracks(int limit) {
        try {
            String token = getAccessToken();

            // Spotify의 "Top 50 Global" 플레이리스트 ID
            String playlistId = "37i9dQZEVXbMDoHDwVN2tF";

            String url = String.format("https://api.spotify.com/v1/playlists/%s/tracks?limit=%d",
                    playlistId, Math.min(limit, 50));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseTracksFromPlaylist(response.body());
            } else {
                log.error("Failed to get popular tracks: {} - {}", response.statusCode(), response.body());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("Error getting popular tracks", e);
            return new ArrayList<>();
        }
    }

    /**
     * 트랙을 검색합니다.
     *
     * @param query 검색어
     * @param limit 가져올 트랙 수
     * @return 트랙 리스트
     */
    public List<Track> searchTracks(String query, int limit) {
        try {
            String token = getAccessToken();
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = String.format("https://api.spotify.com/v1/search?q=%s&type=track&limit=%d",
                    encodedQuery, limit);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseTracksFromSearch(response.body());
            } else {
                log.error("Failed to search tracks: {} - {}", response.statusCode(), response.body());
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("Error searching tracks", e);
            return new ArrayList<>();
        }
    }

    /**
     * 플레이리스트 응답에서 트랙 정보를 파싱합니다.
     */
    private List<Track> parseTracksFromPlaylist(String jsonResponse) {
        List<Track> tracks = new ArrayList<>();
        JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray items = json.getAsJsonArray("items");

        for (int i = 0; i < items.size(); i++) {
            try {
                JsonObject item = items.get(i).getAsJsonObject();
                JsonObject track = item.getAsJsonObject("track");

                if (track == null || track.isJsonNull()) {
                    continue;
                }

                tracks.add(parseTrack(track));
            } catch (Exception e) {
                log.warn("Failed to parse track at index {}", i, e);
            }
        }

        return tracks;
    }

    /**
     * 검색 응답에서 트랙 정보를 파싱합니다.
     */
    private List<Track> parseTracksFromSearch(String jsonResponse) {
        List<Track> tracks = new ArrayList<>();
        JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonObject tracksObj = json.getAsJsonObject("tracks");
        JsonArray items = tracksObj.getAsJsonArray("items");

        for (int i = 0; i < items.size(); i++) {
            try {
                JsonObject track = items.get(i).getAsJsonObject();
                tracks.add(parseTrack(track));
            } catch (Exception e) {
                log.warn("Failed to parse track at index {}", i, e);
            }
        }

        return tracks;
    }

    /**
     * JSON 객체에서 Track 엔티티를 생성합니다.
     */
    private Track parseTrack(JsonObject trackJson) {
        String id = trackJson.get("id").getAsString();
        String name = trackJson.get("name").getAsString();

        JsonArray artistsArray = trackJson.getAsJsonArray("artists");
        String artist = artistsArray.size() > 0
                ? artistsArray.get(0).getAsJsonObject().get("name").getAsString()
                : "Unknown";

        JsonObject album = trackJson.getAsJsonObject("album");
        String albumName = album.get("name").getAsString();

        JsonArray images = album.getAsJsonArray("images");
        String imageUrl = images.size() > 0
                ? images.get(0).getAsJsonObject().get("url").getAsString()
                : "";

        String previewUrl = trackJson.has("preview_url") && !trackJson.get("preview_url").isJsonNull()
                ? trackJson.get("preview_url").getAsString()
                : null;

        String spotifyUrl = trackJson.getAsJsonObject("external_urls").get("spotify").getAsString();

        return Track.builder()
                .id(id)
                .name(name)
                .artist(artist)
                .album(albumName)
                .imageUrl(imageUrl)
                .previewUrl(previewUrl)
                .spotifyUrl(spotifyUrl)
                .build();
    }
}

