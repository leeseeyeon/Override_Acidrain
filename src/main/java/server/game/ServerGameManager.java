package server.game;

public class ServerGameManager{
	private GameSession session;
	private WordManager wordManager;
	private ItemManager itemManager;
	private WinCondition winCondition;
	
	public ServerGameManager(GameSession session, WordManager wordManager,ItemManager itemManager, WinCondition winCondition) {
		this.session = session;
		this.wordManager = wordManager;
		this.itemManager = itemManager;
		this.winCondition = winCondition;
	}
	
	public void startGame() {
		//게임시작 및 초기설정
	}
	public void processInput(int playerId, String word, long typingTime) {
		//입력 단어의 정답 여부 판정
	}
	public void upadateGame(long deltaTime) {
		//단어 낙하 및 아이템 상태 갱신
	}
	/*에러뜸 public GameResult calculateFinalStats() {
		//cpm,정확도, 승자계산
	}*/
	public boolean checkGameOver() {
		return false;
		//게임 종료 조건 확인
	}
	
	public void endGame() {
		//게임 종료 및 결과 처리
	}
	
}