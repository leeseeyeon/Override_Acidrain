package client.lobby;

public class ChatMessage {
    private final String senderNickname;
    private final String content;
    private final boolean isSystemMessage;

    /* 플레이어가 보낸 채팅 */
    public ChatMessage(String senderNickname, String content) {
        this(senderNickname, content, false);
    }

    private ChatMessage(String senderNickname, String content, boolean isSystemMessage) {
        this.senderNickname = senderNickname;
        this.content = content;
        this.isSystemMessage = isSystemMessage;
    }

    /* 시스템 안내 메시지 */
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