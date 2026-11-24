package server.game.rule;
import server.game.player.Player;
import server.game.session.GameSession;
public interface WinCondition {
	public Player determineWinner(GameSession session);
}
