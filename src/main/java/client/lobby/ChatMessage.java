package client.lobby;

/**
 * 채팅창에 표시할 한 줄 (Model)
 *
 * ⚠ 서버 메시지 클래스 common.message.lobby.ChatMessage와 이름이 같음 (패키지가 달라서 가능)
 *    두 클래스를 같이 쓰는 LobbyController에서는 서버 쪽을 전체 이름으로 씀
 * ℹ isSystemMessage: 카운트다운, 입장·퇴장 안내처럼 사람이 보내지 않은 메시지
 */
public class ChatMessage {
    private final String senderNickname;
    private final String content;
    private final boolean isSystemMessage;

    /** 사람이 보낸 채팅 */
    public ChatMessage(String senderNickname, String content) {
        this(senderNickname, content, false);
    }

    private ChatMessage(String senderNickname, String content, boolean isSystemMessage) {
        this.senderNickname = senderNickname;
        this.content = content;
        this.isSystemMessage = isSystemMessage;
    }

    /** 시스템 안내 메시지 */
    public static ChatMessage system(String content) {
        return new ChatMessage(null, content, true);
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public String getContent() {
        return content;
    }

    public boolean isSystemMessage() {
        return isSystemMessage;
    }
}