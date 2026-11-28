package common.message.game;

import java.util.List;

import common.dto.AttackItemWord;
import common.dto.ClientFallingWord;
import common.dto.LastClaimedItem;
import common.dto.PlayerStateData;
import common.message.Message;
import common.message.MessageType;

public class GameStateUpdateMessage extends Message {
	private List<PlayerStateData> playerStatesDatas;
    private List<ClientFallingWord> player1FallingWords; // Player 1 판 단어
    private List<ClientFallingWord> player2FallingWords; // Player 2 판 단어
	private AttackItemWord attackItemWord;
	private List<String> logMessage;
	private LastClaimedItem lastClaimedItem; // 가장 최근 획득 아이템 (없으면 null)
	
	private GameStateUpdateMessage() {
		super(MessageType.GAME_STATE_UPDATE);
	}
	
	public GameStateUpdateMessage(List<PlayerStateData> playerStatesDatas,
			List<ClientFallingWord> player1FallingWords, List<ClientFallingWord> player2FallingWords,
			AttackItemWord attackItemWord, List<String> logMessage, LastClaimedItem lastClaimedItem) {
		this();
		this.playerStatesDatas = playerStatesDatas;
		this.player1FallingWords = player1FallingWords;
		this.player2FallingWords = player2FallingWords;
		this.attackItemWord = attackItemWord;
		this.logMessage = logMessage;
		this.lastClaimedItem = lastClaimedItem;
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
	
	public List<String> getLogMessage() {
	    return logMessage;
	}
	
	public LastClaimedItem getLastClaimedItem() {
		return lastClaimedItem;
	}

}