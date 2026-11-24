package client.game;

public interface GameModelListener {
	//전체적인 게임 상태 업데이트
	public default void onGameStateChanged(GameClientModel model) {}
	
	//단어 상태 업데이트
	public default void onWordsUpdated(GameClientModel model) {}
	
	//플레이어1 상태 업데이트
	public default void onPlayer1StateUpdated(GameClientModel model) {}
	
	//플레이어2 상태 업데이트
	public default void onPlayer2StateUpdated(GameClientModel model) {}

	public void onPlayerStateUpdated(GameClientModel model);
}
