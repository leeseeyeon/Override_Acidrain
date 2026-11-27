package common.message;

import common.message.game.GameFinishedMessage;
import common.message.game.GameStartMessage;
import common.message.game.GameStateUpdateMessage;
import common.message.game.WordSubmitMessage;
import common.message.lobby.ChatMessage;
import common.message.lobby.JoinMessage;
import common.message.lobby.JoinedMessage;
import common.message.lobby.OpponentMessage;
import common.message.lobby.ReadyMessage;
import common.message.lobby.RoleMessage;
import common.message.lobby.ThemeMessage;

// 메시지 type 이름과 그 type을 해석할 메시지 클래스
// 새 메시지 추가 시: ① 여기에 한 줄 ② 새 메시지 클래스 생성 ③ 받는 쪽 switch에 case
public enum MessageType {
    // 대기실 클라 → 서버
    JOIN(JoinMessage.class),               // nickname
    START(SignalMessage.class),            // 필드 없음 (방장만)

    // 대기실 양방향
    CHAT(ChatMessage.class),               // 클라→서버: content / 서버→클라: nickname, content
    READY(ReadyMessage.class),             // ready
    THEME(ThemeMessage.class),             // theme

    // 대기실 서버 → 클라
    JOINED(JoinedMessage.class),           // roomId, role
    OPPONENT(OpponentMessage.class),       // nickname
    OPPONENT_LEFT(SignalMessage.class),    // 필드 없음
    ROLE(RoleMessage.class),               // role
    ERROR(ErrorMessage.class),             // errorCode

    // 게임 클라 → 서버
    WORD_SUBMIT(WordSubmitMessage.class),  // word, typingTime
    GAME_QUIT(SignalMessage.class),        // 필드 없음

    // 게임 서버 → 클라
    GAME_START(GameStartMessage.class),               // gameId, theme, player1, player2
    GAME_STATE_UPDATE(GameStateUpdateMessage.class),  // playerStatesDatas, player1/2FallingWords, attackItemWord, logMessage, lastClaimedItem
    GAME_FINISHED(GameFinishedMessage.class);         // result

    private final Class<? extends Message> messageClass;

    MessageType(Class<? extends Message> messageClass) {
        this.messageClass = messageClass;
    }

    public Class<? extends Message> getMessageClass() {
        return messageClass;
    }
}