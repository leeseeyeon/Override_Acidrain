package server.game.rule;

import server.game.player.Player;
import server.game.session.GameSession;

public class HpZeroWinCondition implements WinCondition {
	
	@Override
	public Player determineWinner(GameSession session){
		
		Player player1 = session.getPlayer1();
		Player player2 = session.getPlayer2();
		
		if (player1.isDead()) {
			return player2;
		}
		
		if (player2.isDead()) {
			return player1;
		}
		
		return null;
	}
}
