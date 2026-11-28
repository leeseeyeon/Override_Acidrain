package server.game.rule;

import server.game.player.Player;
import server.game.session.GameSession;

public class HpZeroWinCondition implements WinCondition {
	
	@Override
	public Player determineWinner(GameSession session){
		
		Player player1 = session.getPlayer1();
		Player player2 = session.getPlayer2();
		
		//player 1이 죽었으면 player2를 위너로 반환
		if (player1.isDead()) {
			return player2;
		}
		
		//player 2가 죽었으면 player1을 위너로 반환
		if (player2.isDead()) {
			return player1;
		}
		
		return null;
	}
}
