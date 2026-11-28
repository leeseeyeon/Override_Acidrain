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
	(13, 1, '왁자지껄'),
	(14, 1, '우왕자왕'),
	(15, 1, '쌉쌀하다'),
	(16, 1, '읊조리다'),
	(17, 1, '국립중앙과학관'),
	(18, 1, '한국정보통신진흥협회'),
	(19, 1, '개인정보보호법'),
	(20, 1, '경찰청창살쇠창살'),
	(21, 1, '내가그린기린그림'),
	(22, 1, '목이긴기린그림'),
	(23, 1, '폴리테트라풀루오로에틸렌'),
	(24, 1, '테트라하이드로푸란'),
	(25, 2, '동적메모리할당'),
	(26, 2, '멀티스레드프로그래밍'),
	(27, 2, 'heap'),
	(28, 2, 'malloc'),
	(29, 2, '메모리'),
	(30, 2, '이중연결리스트'),
	(31, 2, '버블정렬'),
	(32, 2, '파이프라이닝'),
	(33, 2, '부동소수점'),
	(34, 2, '레지스터'),
	(35, 2, '알고리즘'),
	(36, 2, '정적바인딩'),
	(37, 2, '동적바인딩'),
	(38, 2, '싱글턴패턴'),
	(39, 2, '팩토리메서드패턴'),
	(40, 2, '개방폐쇄원칙'),
	(41, 2, '단일책임원칙'),
	(42, 2, '의존성주입'),
	(43, 2, 'Override'),
	(44, 2, 'Integer'),
	(45, 2, '생성자'),
	(46, 2, '컴파일러'),
	(47, 2, 'fetch'),
	(48, 2, '스레드'),
	(49, 2, '프로세스'),
	(50, 2, '재귀함수'),
	(51, 2, '이진탐색'),
	(52, 2, 'UTF-8'),
	(53, 2, '2의보수'),
	(54, 2, '7segment'),
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
