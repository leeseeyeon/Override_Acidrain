package common.message.game;

import common.dto.PlayerProfile;
import common.message.Message;
import common.message.MessageType;
import common.type.Theme;

public class GameStartMessage extends Message {
    private int gameId;
    private Theme theme;
    private PlayerProfile player1;
    private PlayerProfile player2;

    // Gson이 받을 때 쓰는 생성자
    private GameStartMessage() {
        super(MessageType.GAME_START);
    }

    // 서버가 보낼 때 쓰는 생성자 (player1 = 방장, player2 = 참가자)
    public GameStartMessage(int gameId, Theme theme, PlayerProfile player1, PlayerProfile player2) {
        this();
        this.gameId = gameId;
        this.theme = theme;
        this.player1 = player1;
        this.player2 = player2;
    }

    public int getGameId() {
    	return gameId;
    }
    
    public Theme getTheme() {
    	return theme;
    }
    
    public PlayerProfile getPlayer1() {
    	return player1;
    }
    
    public PlayerProfile getPlayer2() {
    	return player2;
    }
}