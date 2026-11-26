package server.game.session;
import server.game.player.*;
import common.type.Theme;


public class GameSession {
	private int gameId;
	private Player player1;
	private Player player2;
	private GameStatus gameStatus;
	private long startTime;
	private long endTime;
	private final Theme selectedTheme; // common값 사용. 참조하기
	private Player winner;
	
	//게임세션 생성자
	public GameSession(int gameId, Player player1, Player player2,Theme theme) {
		this.gameId = gameId;
		this.player1 = player1;
		this.player2 = player2;
		this.gameStatus = GameStatus.READY;
		this.startTime = 0;
		this.endTime = 0;
		this.selectedTheme = theme;
		this.winner = null;
	}
	
	//플레이어 ID를 기준으로 플레이어 반환
	public Player getPlayer(int id) {
		if (player1.getPlayerId() == id) {
			return player1;
		}
		
		if (player2.getPlayerId() == id) {
			return player2;
		}
		return null;
	}
    // 플레이어 ID를 기준으로 상대방 반환
    public Player getOpponent(int id) {
        if (player1.getPlayerId() == id) {
            return player2;
        }

        if (player2.getPlayerId() == id) {
            return player1;
        }

        return null;
    }


    // 테마 반환
    public Theme getTheme() {
        return selectedTheme;
    }

    // 승자 설정
    public void setWinner(Player player) {
        this.winner = player;
    }

    // 게임 상태 설정
    public void setStatus(GameStatus status) {
        this.gameStatus = status;
    }

    // 게임 시작 시간 설정
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    // 게임 종료 시간 설정
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    // Getter
    public int getGameId() {
        return gameId;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public GameStatus getStatus() {
        return gameStatus;
    }

    public Player getWinner() {
        return winner;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
	
	
	
}
	
