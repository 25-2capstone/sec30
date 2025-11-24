-- prevent empty script error during initialization
-- Insert test users (password: "password" encrypted with BCrypt)
INSERT INTO users (email, password, nickname, name, create_at) VALUES
('alex.johnson@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye7J2qV6ZPIU2N/FLQKDHJnLVwbFHqJie', 'alex.johnson', 'Alex J', CURRENT_TIMESTAMP);

-- You can add more test data here later

