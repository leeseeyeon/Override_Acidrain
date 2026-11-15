package common.message;

public class MessageType {
    // 대기실 클라 → 서버
    public static final String JOIN  = "JOIN";   // nickname
    public static final String READY = "READY";  // ready (양방향)
    public static final String THEME = "THEME";  // theme (양방향)
    public static final String START = "START";  // 필드 없음 (방장만)
    public static final String CHAT  = "CHAT";   // content

    // 대기실 서버 → 클라
    public static final String JOINED        = "JOINED";        // roomId, role
    public static final String OPPONENT      = "OPPONENT";      // nickname
    public static final String GAME_START    = "GAME_START";    // (게임 데이터 추가 예정)
    public static final String OPPONENT_LEFT = "OPPONENT_LEFT"; // 필드 없음
    public static final String ROLE          = "ROLE";          // role
    public static final String ERROR         = "ERROR";         // error code
    // CHAT은 서버 → 클라에도 같은 이름 사용: nickname, content
    // READY는 서버 → 클라에도 같은 이름 사용: ready
    // THEME은 서버 → 클라에도 같은 이름 사용: theme
    
    
    // 게임 클라 → 서버
    
    
    
    
    // 게임 서버 → 클라

    private MessageType() {}
}
