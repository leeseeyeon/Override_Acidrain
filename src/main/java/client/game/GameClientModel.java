package client.game;

import java.awt.Graphics;
import java.util.List;

public class GameClientModel {
	private PlayerStateData Player1State;
	private PlayerStateData Player2State;
	private List<ClientFallingWord> activeFallingWords;
	private AttackItemWord conterItemWord;
	private List<String> logs;
	private List<GameModelLIstener> listeners;
	
	public void applyServerUpdate(GameStateUpdate update) {
		//TODO : 서버에게서 현 게임 상태를 전달받은
	}
	public void addListener(GameModelListener listener) {
		// TODO: [채영] 전달받은 리스너(listener)를 널(null) 체크 후, 
	    // 내부 리스너 목록(listeners 리스트)에 추가하기
	    // 예시: 
	    // if (listener != null && !listeners.contains(listener)) {
	    //     listeners.add(listener);
	    // }
	}
	public void notifyGameStateChanged() {
		// TODO : 
	}
}
