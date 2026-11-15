package common.message.lobby;

import common.message.Message;
import common.message.MessageType;

public class SignalMessage extends Message {
    private SignalMessage(String type) {
        super(type);
    }

    // type만 있고 내용은 없는 메시지를 전부 담당하는 클래스
    public static SignalMessage start()        { return new SignalMessage(MessageType.START); }
    public static SignalMessage opponentLeft() { return new SignalMessage(MessageType.OPPONENT_LEFT); }
}
