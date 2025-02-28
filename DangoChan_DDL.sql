-- 2025-02-20
-- 데이터베이스 사용 : scit
create database DangoChan;

drop database DangoChan;

use DangoChan;

-- 유저 정보 테이블
DROP TABLE IF EXISTS users;

CREATE TABLE `users` (
	user_id	BIGINT	AUTO_INCREMENT PRIMARY KEY,
	email	VARCHAR(50)	UNIQUE,
	user_name	VARCHAR(50)	NOT NULL UNIQUE,
	auth_type	VARCHAR(50) NOT NULL check(auth_type in ('LOCAL', 'GOOGLE', 'APPLE', 'LINE', 'GUEST')),
	password	VARCHAR(255),
	provider_id	VARCHAR(255),
	roles    varchar(50) DEFAULT 'ROLE_USER' check(roles in ('ROLE_USER', 'ROLE_ADMIN')),
	created_at	DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 카테고리 테이블
DROP TABLE IF EXISTS `category`;

CREATE TABLE `category` (
    `category_id` BIGINT AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `category_name` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`category_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON delete CASCADE
);


-- 덱 테이블
DROP TABLE IF EXISTS `deck`;

CREATE TABLE `deck` (
    `deck_id` BIGINT AUTO_INCREMENT NOT NULL,
    `category_id` BIGINT NOT NULL,
--     `user_id` BIGINT NOT NULL,
    `deck_name` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`deck_id`),
    FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON delete CASCADE
-- 	FOREIGN KEY (`user_id`) REFERENCES `category` (`user_id`) ON delete CASCADE
    );

-- 카드 테이블
DROP TABLE IF EXISTS `card`;
CREATE TABLE `card` (
    `card_id` BIGINT AUTO_INCREMENT NOT NULL,
    `deck_id` BIGINT NOT NULL,
--     `category_id` BIGINT NOT NULL,
--     `user_id` BIGINT NOT NULL,
    `word` VARCHAR(100) NOT NULL,
    `pos` VARCHAR(50),
    `meaning` VARCHAR(255),
    `example_jp` VARCHAR(500),
    `example_kr` VARCHAR(500),
    `study_level` INT default 1,
    PRIMARY KEY (`card_id`),
    FOREIGN KEY (`deck_id`) REFERENCES `deck` (`deck_id`) ON delete CASCADE
-- 	FOREIGN KEY (`category_id`) REFERENCES `deck` (`category_id`) ON delete CASCADE,
--     FOREIGN KEY (`user_id`) REFERENCES `deck` (`user_id`) ON delete CASCADE
    );


-- 학습시간 기록 테이블
DROP TABLE IF EXISTS `deck_study_time`;
CREATE TABLE `deck_study_time` (
    `study_time_id` BIGINT AUTO_INCREMENT NOT NULL,
    `deck_id` BIGINT NOT NULL,
--     `user_id` BIGINT NOT NULL,
    `study_time` INT,
    `date` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`study_time_id`),
    FOREIGN KEY (`deck_id`) REFERENCES `deck` (`deck_id`) ON delete CASCADE
--     FOREIGN KEY (`user_id`) REFERENCES `deck` (`user_id`) ON delete CASCADE
);


-- 게시판 테이블
-- 기존에 scit에서 사용하던 board 테이블을 삭제해야 사용 가능
-- spring7 프로젝트 확인을 해야할 경우 신중하게 삭제해주세요.
DROP TABLE IF EXISTS `board`;
CREATE TABLE `board` (
    `board_id` INT AUTO_INCREMENT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `title` varchar(255) not null,
    `word_count` INT,
    `views` INT default 0,
    `board_content` VARCHAR(500),
    `original_file_name` varchar(500),
    `saved_file_name` varchar(500),
    `create_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `modify_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`board_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);

select * from board;

-- 게시물 좋아요 테이블
DROP TABLE IF EXISTS `board_likes`;
CREATE TABLE `board_likes` (
    `board_like_id` BIGINT AUTO_INCREMENT NOT NULL,
    `board_id` INT NOT NULL,
    `user_id` BIGINT NOT NULL,
    PRIMARY KEY (`board_like_id`),
    FOREIGN KEY (`board_id`) REFERENCES `board` (`board_id`) ON delete cascade,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON delete CASCADE
    );

-- 댓글 테이블
DROP TABLE IF EXISTS `reply`;
CREATE TABLE `reply` (
    `reply_id` INT AUTO_INCREMENT NOT NULL,
    `board_id` INT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `content` VARCHAR(100) NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `parent_reply_id` INT,
    PRIMARY KEY (`reply_id`),
    FOREIGN KEY (`board_id`) REFERENCES `board` (`board_id`) ON delete CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON delete CASCADE
    );

   
-- 추가 제작해야하는 테이블
-- 대댓글(답글) 테이블
-- 채팅 관련 테이블
   
   
   
-- 샘플 데이터
-- users 테이블 데이터 삽입
INSERT INTO `users` (`user_id`, `email`, `user_name`, `auth_type`, `password`, `provider_id`, `created_at`) VALUES
(1, 'user1@example.com', 'User One', 'LOCAL', 'password123', NULL, NOW()),
(2, 'user2@example.com', 'User Two', 'GOOGLE', '2', 'google_12345', NOW());

-- category 테이블 데이터 삽입
INSERT INTO `category` (`category_id`, `user_id`, `category_name`) VALUES
(1, 1, 'JLPT N1'),
(2, 2, 'Business Japanese');

-- deck 테이블 데이터 삽입 , `user_id`
INSERT INTO `deck` (`deck_id`, `category_id`, `deck_name`) VALUES
(1, 1, 'N1 Vocabulary'),
(2, 2, 'Business Expressions');

-- card 테이블 데이터 삽입
INSERT INTO `card` (`card_id`, `deck_id`, `word`, `pos`, `meaning`, `example_jp`, `example_kr`, `study_level`) VALUES
(1, 1, '挑戦', '名詞', '도전', '新しい仕事に挑戦する。', '마ㅏ', 3),
(2, 2, '契約', '名詞', '계약', '新しい契約を締結する。', '아아', 2);


-- deckStudyTime 테이블 데이터 삽입
INSERT INTO `deck_study_time` (`study_time_id`, `deck_id`, `study_time`, `date`) VALUES
(1, 1, NOW(), CURDATE()),
(2, 2, NOW(), CURDATE());

-- board 테이블 데이터 삽입
INSERT INTO `board` (`board_id`, `user_id`, `title`, `word_count`, `views`, `original_file_name`, `create_date`, `modify_date`) VALUES
(1, 1, '스터디노트', 500, 100, 'study_notes.pdf', NOW(), NOW()),
(2, 2, '한자 외우기 팁', 300, 50, 'kanji_tips.pdf', NOW(), NOW());

-- boardLikes 테이블 데이터 삽입
INSERT INTO `board_likes` (`board_like_id`, `board_id`, `user_id`) VALUES
(1, 1, 2),
(2, 2, 1);

-- reply 테이블 데이터 삽입
INSERT INTO `reply` (`reply_id`, `board_id`, `user_id`, `content`, `created_at`, `parent_reply_id`) VALUES
(1, 1, 2, '좋은 정보 감사합니다!', NOW(), 0),
(2, 2, 1, '도움이 되었다니 다행입니다!', NOW(), 1);


use dangochan;
-- 샘플 데이터 조회
-- users 테이블 데이터 조회
SELECT * FROM `users`;

-- category 테이블 데이터 조회
SELECT * FROM `category`;

-- deck 테이블 데이터 조회
SELECT * FROM `deck`;
commit;

-- card 테이블 데이터 조회
SELECT * FROM `card`;

-- deckStudyTime 테이블 데이터 조회
SELECT * FROM `deck_study_time`;

-- board 테이블 데이터 조회
SELECT * FROM `board`;

-- boardLikes 테이블 데이터 조회
SELECT * FROM `board_likes`;

-- reply 테이블 데이터 조회
SELECT * FROM `reply`;

