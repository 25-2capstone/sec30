-- prevent empty script error during initialization
-- Insert test users (password: "password" encrypted with BCrypt)
INSERT INTO users (username, password, email, profile_image, created_at) VALUES
('alex.johnson', '$2a$10$N9qo8uLOickgx2ZMRZoMye7J2qV6ZPIU2N/FLQKDHJnLVwbFHqJie', 'alex.johnson@example.com', NULL, CURRENT_TIMESTAMP);

-- You can add more test data here later

