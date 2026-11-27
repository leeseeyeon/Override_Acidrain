package server.game.rule;
import server.game.player.Player;
import server.game.session.GameSession;

//승리조건 인터페이스_게임세션당 위너 생성
public interface WinCondition {
	public Player determineWinner(GameSession session);
}
