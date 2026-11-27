package common.config;

//서버와 클라이언트가 공통으로 사용하는 게임 설정값
public final class GameConfig {

 // 게임 필드 크기
 public static final int FIELD_WIDTH = 300;
 public static final int FIELD_HEIGHT = 400;

 // 플레이어 설정
 public static final int MAX_HP = 100;

 // 게임 시작 설정
 public static final int START_COUNTDOWN_SECONDS = 3;

 // 객체 생성 방지
 private GameConfig() {
 }
}
