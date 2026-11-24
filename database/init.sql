-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        11.4.13-MariaDB - MariaDB Server
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  12.22.0.7382
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- wordstheme1 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `wordstheme1` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_croatian_ci */;
USE `wordstheme1`;

-- 테이블 wordstheme1.words 구조 내보내기
CREATE TABLE IF NOT EXISTS `words` (
  `word_id` int(11) NOT NULL AUTO_INCREMENT,
  `theme_id` int(11) NOT NULL,
  `word_text` varchar(100) NOT NULL,
  PRIMARY KEY (`word_id`)
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_croatian_ci;

-- 테이블 데이터 wordstheme1.words:~94 rows (대략적) 내보내기
INSERT INTO `words` (`word_id`, `theme_id`, `word_text`) VALUES
	(1, 1, '사과'),
	(2, 1, '바나나'),
	(3, 1, '딸기'),
	(4, 1, '복숭아'),
	(5, 1, '블루베리'),
	(6, 1, '동해물'),
	(7, 1, '백두산'),
	(8, 1, '독도는우리땅'),
	(9, 1, '이화여자대학교'),
	(10, 1, '강아지'),
	(11, 1, '무구정광대다라니경'),
	(12, 1, '쌀국수'),
	(13, 1, '집에가고싶다'),
	(14, 1, '팀플너무힘들어요'),
	(15, 1, '살려주세요'),
	(16, 1, '엄마보고싶다'),
	(17, 1, '아빠'),
	(18, 1, '꿈꾸던어른이되었나요'),
	(19, 1, '리센느'),
	(20, 1, '대머리'),
	(21, 1, '배고플땐마라쌀국수'),
	(22, 1, '우사기'),
	(23, 1, '이얏하푸루푸루야하이히히야하'),
	(24, 1, '우나아'),
	(25, 2, '컴퓨터'),
	(26, 2, '모니터'),
	(27, 2, '키보드'),
	(28, 2, '마우스'),
	(29, 2, '메모리'),
	(30, 2, '반도체'),
	(31, 2, '서버'),
	(32, 2, '클라이언트'),
	(33, 2, '네트워크'),
	(34, 2, '인터넷'),
	(35, 2, '알고리즘'),
	(36, 2, '자료구조'),
	(37, 2, '운영체제'),
	(38, 2, '데이터베이스'),
	(39, 2, '프로그래밍'),
	(40, 2, '객체지향'),
	(41, 2, '캡슐화'),
	(42, 2, '다형성'),
	(43, 2, '상속'),
	(44, 2, '인터페이스'),
	(45, 2, '생성자'),
	(46, 2, '컴파일러'),
	(47, 2, '디버깅'),
	(48, 2, '스레드'),
	(49, 2, '프로세스'),
	(50, 2, '재귀함수'),
	(51, 2, '이진탐색'),
	(52, 2, '인공지능'),
	(53, 2, '소프트웨어'),
	(54, 2, '하드웨어'),
	(55, 3, 'apple'),
	(56, 3, 'dream'),
	(57, 3, 'happy'),
	(58, 3, 'smile'),
	(59, 3, 'cloud'),
	(60, 3, 'music'),
	(61, 3, 'heart'),
	(62, 3, 'ocean'),
	(63, 3, 'light'),
	(64, 3, 'peace'),
	(65, 3, 'flower'),
	(66, 3, 'summer'),
	(67, 3, 'winter'),
	(68, 3, 'coffee'),
	(69, 3, 'friend'),
	(70, 3, 'travel'),
	(71, 3, 'planet'),
	(72, 3, 'forest'),
	(73, 3, 'cookie'),
	(74, 3, 'secret'),
	(75, 3, 'rainbow'),
	(76, 3, 'freedom'),
	(77, 3, 'morning'),
	(78, 3, 'journey'),
	(79, 3, 'picture'),
	(80, 3, 'chicken'),
	(81, 3, 'sunshine'),
	(82, 3, 'mountain'),
	(83, 3, 'birthday'),
	(84, 3, 'beautiful'),
	(85, 3, 'chocolate'),
	(86, 3, 'adventure'),
	(87, 3, 'butterfly'),
	(88, 3, 'wonderful'),
	(89, 3, 'happiness'),
	(90, 3, 'university'),
	(91, 3, 'imagination'),
	(92, 3, 'opportunity'),
	(93, 3, 'strawberry'),
	(94, 3, 'responsibility');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
