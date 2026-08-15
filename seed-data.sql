-- Seed data for LinkedIn-like microservices
-- Run the appropriate sections against each database.
-- User Service uses PostgreSQL database: userDB
-- Posts Service uses PostgreSQL database: postsDB
-- Notification Service uses PostgreSQL database: notificationDB
-- Connections Service uses Neo4j.

-- ===================================================================
-- USER SERVICE / PostgreSQL (userDB)
-- ===================================================================
-- If you use psql:
--   \\c userDB
--   \i seed-data.sql

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    password TEXT NOT NULL
);

TRUNCATE TABLE users RESTART IDENTITY CASCADE;

INSERT INTO users (name, email, password) VALUES
('Alice Example', 'alice@example.com', '$2a$10$N9qo8uLOickgx2ZMRZo5i.uRvZ5.insC2gq7hQ.nmyx/ZQvFZ8S6'),
('Bob Example', 'bob@example.com', '$2a$10$N9qo8uLOickgx2ZMRZo5i.uRvZ5.insC2gq7hQ.nmyx/ZQvFZ8S6'),
('Carol Example', 'carol@example.com', '$2a$10$N9qo8uLOickgx2ZMRZo5i.uRvZ5.insC2gq7hQ.nmyx/ZQvFZ8S6');

-- Login password for all seeded users: password

-- ===================================================================
-- POSTS SERVICE / PostgreSQL (postsDB)
-- ===================================================================
CREATE TABLE IF NOT EXISTS posts (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP(6)
);

CREATE TABLE IF NOT EXISTS post_likes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMP(6)
);

TRUNCATE TABLE post_likes RESTART IDENTITY CASCADE;
TRUNCATE TABLE posts RESTART IDENTITY CASCADE;

INSERT INTO posts (content, user_id, created_at) VALUES
('Hello world from Alice!', 1, NOW()),
('Bob shares a second post', 2, NOW()),
('Carol writes her first post', 3, NOW());

INSERT INTO post_likes (user_id, post_id, created_at) VALUES
(2, 1, NOW()),
(3, 1, NOW()),
(1, 2, NOW());

-- ===================================================================
-- NOTIFICATION SERVICE / PostgreSQL (notificationDB)
-- ===================================================================
CREATE TABLE IF NOT EXISTS notification (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    message TEXT,
    local_date_time TIMESTAMP(6)
);

TRUNCATE TABLE notification RESTART IDENTITY CASCADE;

INSERT INTO notification (user_id, message, local_date_time) VALUES
(1, 'Welcome Alice! Your account is ready.', NOW()),
(2, 'Bob, you have a new connection recommendation.', NOW());

-- ===================================================================
-- CONNECTIONS SERVICE / Neo4j
-- ===================================================================
-- Run these Cypher commands in Neo4j Browser or cypher-shell.
-- Example: cypher-shell -u neo4j -p password "@seed-data.sql"

// Create person nodes for user IDs used by user-service
MERGE (alice:Person {userId: 1, name: 'Alice Example'});
MERGE (bob:Person {userId: 2, name: 'Bob Example'});
MERGE (carol:Person {userId: 3, name: 'Carol Example'});

// Existing connection between Alice and Bob
MERGE (alice)-[:CONNECTED_TO]-(bob);

// Pending request from Carol to Alice
MERGE (carol)-[:REQUESTED_TO]->(alice);

-- ===================================================================
-- Notes for manual API testing
-- ===================================================================
-- User Service endpoints (http://localhost:9020):
--   POST /auth/signup
--   POST /auth/login
--
-- Posts Service endpoints (http://localhost:9010):
--   POST /posts/core
--   GET /posts/core/{postId}           [header X-User-Id]
--   GET /posts/core/users/{userId}/allPosts
--   POST /posts/likes/{postId}         [header X-User-Id]
--   DELETE /posts/likes/{postId}       [header X-User-Id]
--
-- Connections Service endpoints (http://localhost:9040):
--   GET /connections/core/first-degree [header X-User-Id]
--   POST /connections/core/request/{userId}  [header X-User-Id]
--   POST /connections/core/accept/{userId}   [header X-User-Id]
--   POST /connections/core/reject/{userId}   [header X-User-Id]
--
-- Important: services use X-User-Id request header to determine the current user.
-- The seeded users are:
--   userId=1 -> alice@example.com / password
--   userId=2 -> bob@example.com / password
--   userId=3 -> carol@example.com / password
