package common.message;

public class MessageType {
    // 클라 → 서버
    public static final String JOIN  = "JOIN";   // nickname
    public static final String READY = "READY";  // ready (참가자만)
    public static final String THEME = "THEME";  // theme (방장만)
    public static final String START = "START";  // 필드 없음 (방장만)
    public static final String CHAT  = "CHAT";   // content

    // 서버 → 클라
    public static final String JOINED        = "JOINED";        // roomId, role
    public static final String OPPONENT      = "OPPONENT";      // nickname
    public static final String READY_STATE   = "READY_STATE";   // ready
    public static final String THEME_CHANGED = "THEME_CHANGED"; // theme
    public static final String GAME_START    = "GAME_START";    // (게임 데이터 추가 예정)
    public static final String OPPONENT_LEFT = "OPPONENT_LEFT"; // 필드 없음
    public static final String ROLE          = "ROLE";          // role
    public static final String ERROR         = "ERROR";         // reason
    // CHAT은 서버 → 클라에도 같은 이름 사용: nickname, content

    private MessageType() {}
}
