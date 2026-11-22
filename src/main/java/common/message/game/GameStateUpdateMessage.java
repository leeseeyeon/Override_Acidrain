package common.message.game;

import common.message.Message;
import common.message.MessageType;

public class GameStateUpdateMessage extends Message {
	
	private GameStateUpdateMessage() {
		super(MessageType.GAME_STATE_UPDATE);
	}

}
