package client.game;

import java.util.ArrayList;
import java.util.List;

import common.message.game.PlayerStateData;

public class GameClientModel {
	private List<PlayerStateData> playerstates = new ArrayList<>();;
	private PlayerStateData Player1State;
	private PlayerStateData Player2State;
	private List<ClientFallingWord> activeFallingWords = new ArrayList<>();
	private AttackItemWord centerItemWord;
	private List<String> logs = new ArrayList<>();
	private List<GameModelListener> listeners = new ArrayList<>();
	
	//상황별 게임 상태 업데이트
	public void applyServerUpdate(Game_State_Update update) {
		if (update == null) { return; }
		
		//플래그 설정(가져오고 싶은 값별로 상황을 나눔)
		boolean wordsChanged = false;
	    boolean p1Changed = false;
	    boolean p2Changed = false;
	    boolean stateChanged = false;
	    
	    //player1,2 상태 업데이트
	    if (update.getPlayerStates() != null) {
	        for (PlayerStateData state : update.getPlayerStates()) {
	            if (state == null) continue;
	            if (state.getplayerId() == 1) {
	                this.Player1State = state;
	                p1Changed = true;
	            } else if (state.getplayerId() == 2) {
	                this.Player2State = state;
	                p2Changed = true;
	            }
	        }
	    }
	    
	    // 떨어지는 단어 목록 반영
	    if (update.getFallingWords() != null) {
	        this.activeFallingWords = new ArrayList<>(update.getFallingWords());
	        wordsChanged = true;
	    }
	    
	    // 중앙 제시 단어 반영
	    if (update.getCenterPromptWord() != null) {
	        this.centerItemWord = update.getCenterPromptWord();
	        stateChanged = true;
	    }
	    
	    // 로그 메시지 추가
	    if (update.getLogMessage() != null && !update.getLogMessage().isBlank()) {
	        this.logs.add(update.getLogMessage());
	        stateChanged = true;
	    }

	    // 변경된 항목에 맞춰 리스너 통지
	    if (wordsChanged) {
	        notifyWordsUpdated();
	    }
	    if (p1Changed) {
	        notifyPlayer1StateUpdated();
	    }
	    if (p2Changed) {
	        notifyPlayer2StateUpdated();
	    }
	    if (stateChanged) {
	        notifyGameStateChanged();
	    }
	}
	
	//전달받은 리스너를 내부 리스너 목록에 추가하기
	public void addListener(GameModelListener listener) {
	    if (listener != null && !listeners.contains(listener)) {
	         listeners.add(listener);
	     }
	}
	
	//상태 업데이트 알림
	public void notifyWordsUpdated() {
		if (listeners == null || listeners.size() == 0 ) {
			return;
		}
		for (GameModelListener listener : new ArrayList<>(listeners)) {
			listener.onWordsUpdated(this);
		}
	}
	public void notifyPlayer1StateUpdated() {
		if (listeners == null || listeners.size() == 0 ) {
			return;
		}
		for (GameModelListener listener : new ArrayList<>(listeners)) {
			listener.onPlayer1StateUpdated(this);
		}
	}
	public void notifyPlayer2StateUpdated() {
		if (listeners == null || listeners.size() == 0 ) {
			return;
		}
		for (GameModelListener listener : new ArrayList<>(listeners)) {
			listener.onPlayer2StateUpdated(this);
		}
	}
	public void notifyGameStateChanged() {
		if (listeners == null || listeners.size() == 0 ) {
			return;
		}
		for (GameModelListener listener : new ArrayList<>(listeners)) {
			listener.onGameStateChanged(this);
		}
	}
	
	//getter
	public PlayerStateData getPlayer1state() {
		return Player1State;
	}
	public PlayerStateData getPlayer2state() {
		return Player2State;
	}
	public List<PlayerStateData> getPlayerStateList(){
		return playerstates;
	}
	public List<ClientFallingWord> getActiveFallingWords(){
		return activeFallingWords;
	}
	public List<String> getLog(){
		return logs;
	}
}
