package common.message.game;

import java.util.List;

import common.message.Message;
import common.message.MessageType;

public class GameStateUpdateMessage extends Message {
	private List<PlayerStateData> playerStatesDatas;
    private List<ClientFallingWord> fallingWords;
	private AttackItemWord attackItemWord;
	private List<String> logMessage;
	
	private GameStateUpdateMessage() {
		super(MessageType.GAME_STATE_UPDATE);
	}
	
	public GameStateUpdateMessage(List<PlayerStateData> playerStatesDatas, List<ClientFallingWord> fallingWords, AttackItemWord attackItemWord) {
		this();
		this.playerStatesDatas = playerStatesDatas;
		this.fallingWords = fallingWords;
		this.attackItemWord = attackItemWord;
	}
	
	public List<PlayerStateData> getPlayerStatesDatas() {
		return playerStatesDatas;
	}

	public List<ClientFallingWord> getFallingWords() {
		return fallingWords;
	}

	public AttackItemWord getAttackItemWord() {
		return attackItemWord;
	}
	public List<String> getLogMessage() {
	    return logMessage;
	}

}
