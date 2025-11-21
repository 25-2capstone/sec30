-- 샘플 데이터 삽입 (MySQL 프로필에서만 사용)

-- 사용자
INSERT INTO users (username, password, email, nickname, profile_image_url, bio, created_at)
VALUES
('testuser', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IVD5bBePpNdAp0PqPPBSZ4iOeYd9ma', 'test@example.com', 'Test User', 'https://via.placeholder.com/150', '음악을 사랑하는 사람', NOW()),
('alex', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IVD5bBePpNdAp0PqPPBSZ4iOeYd9ma', 'alex@example.com', 'Alex Johnson', 'https://via.placeholder.com/150', '다양한 음악 취향', NOW());

-- 플레이리스트
INSERT INTO playlists (title, description, cover_image_url, user_id, is_public, created_at, updated_at)
VALUES
('Chill Vibes Mix', '편안한 음악 모음', 'https://via.placeholder.com/300x300?text=Chill+Vibes', 1, true, NOW(), NOW()),
('Workout Hits', '운동할 때 듣기 좋은 음악', 'https://via.placeholder.com/300x300?text=Workout', 1, true, NOW(), NOW()),
('Late Night Focus', '밤에 집중할 때 좋은 음악', 'https://via.placeholder.com/300x300?text=Focus', 1, true, NOW(), NOW()),
('K-Pop Daebak', 'K-POP 명곡 모음', 'https://via.placeholder.com/300x300?text=KPOP', 2, true, NOW(), NOW());
