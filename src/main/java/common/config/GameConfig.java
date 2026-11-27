package common.config;

// 서버와 클라이언트가 공유하는 게임 공통 설정값
public final class GameConfig {
    // 낙하 영역 크기
    public static final int FIELD_WIDTH = 300;
    public static final int FIELD_HEIGHT = 400;

    // 기본 체력 및 시작 카운트다운(초)
    public static final int MAX_HP = 100;
    public static final int START_COUNTDOWN_SECONDS = 3;

    private GameConfig() {
    }
}