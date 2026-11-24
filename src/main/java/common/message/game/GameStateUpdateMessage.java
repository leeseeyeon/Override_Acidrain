package common.message.game;

import java.util.List;

import common.message.Message;
import common.message.MessageType;

public class GameStateUpdateMessage extends Message {
	private List<PlayerStateData> playerStatesDatas;
    private List<ClientFallingWord> player1FallingWords; // Player 1 판 단어
    private List<ClientFallingWord> player2FallingWords; // Player 2 판 단어
	private AttackItemWord attackItemWord;
    
	private GameStateUpdateMessage() {
		super(MessageType.GAME_STATE_UPDATE);
	}
	
	public GameStateUpdateMessage(List<PlayerStateData> playerStatesDatas,
			List<ClientFallingWord> player1FallingWords, List<ClientFallingWord> player2FallingWords,
			AttackItemWord attackItemWord) {
		this();
		this.playerStatesDatas = playerStatesDatas;
		this.player1FallingWords = player1FallingWords;
		this.player2FallingWords = player2FallingWords;
		this.attackItemWord = attackItemWord;
	}
	
	public List<PlayerStateData> getPlayerStatesDatas() {
		return playerStatesDatas;
	}

	public List<ClientFallingWord> getPlayer1FallingWords() {
		return player1FallingWords;
	}

	public List<ClientFallingWord> getPlayer2FallingWords() {
		return player2FallingWords;
	}

	public AttackItemWord getAttackItemWord() {
		return attackItemWord;
	}

}