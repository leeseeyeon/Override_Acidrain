package common.message;

public class SignalMessage extends Message {
    private SignalMessage(MessageType type) {
        super(type);
    }

    // type만 있고 내용은 없는 메시지를 전부 담당하는 클래스
    public static SignalMessage start()        { return new SignalMessage(MessageType.START); }
    public static SignalMessage opponentLeft() { return new SignalMessage(MessageType.OPPONENT_LEFT); }
    public static SignalMessage gameQuit()    { return new SignalMessage(MessageType.GAME_QUIT); }
}
