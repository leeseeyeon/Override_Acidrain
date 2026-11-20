package common.message.game;

import common.GameResult;
import common.message.Message;
import common.message.MessageType;

public class GameFinishedMessage extends Message {
    private GameResult result;

    private GameFinishedMessage() {
        super(MessageType.GAME_FINISHED);
    }

    // 서버가 보낼 때
    public GameFinishedMessage(GameResult result) {
        this();
        this.result = result;
    }

    public GameResult getResult() {
    	return result;
    }
}