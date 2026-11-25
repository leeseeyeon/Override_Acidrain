package client.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import common.message.game.AttackItemWord;
import common.message.game.ClientFallingWord;
import common.message.game.GameStateUpdateMessage;
import common.message.game.PlayerStateData;

import common.message.game.PlayerStateData;

public class GameClientModel {
	private int localPlayerId = -1;
	private PlayerStateData Player1State; // ID가 작은 플레이어
	private PlayerStateData Player2State; // ID가 큰 플레이어
	private List<ClientFallingWord> player1FallingWords = new ArrayList<>();
	private List<ClientFallingWord> player2FallingWords = new ArrayList<>();
	private AttackItemWord centerItemWord;
	private List<String> logs = new ArrayList<>();
	private List<GameModelListener> listeners = new ArrayList<>();

	// 상황별 게임 상태 업데이트
	public void applyServerUpdate(GameStateUpdateMessage update) {
		if (update == null) {
			return;
		}

		// 플래그 설정(가져오고 싶은 값별로 상황을 나눔)
		boolean wordsChanged = false;
		boolean p1Changed = false;
		boolean p2Changed = false;
		boolean stateChanged = false;

		// player1(작은 ID), player2(큰 ID) 상태 업데이트
		if (update.getPlayerStatesDatas() != null && !update.getPlayerStatesDatas().isEmpty()) {
			List<PlayerStateData> validStates = new ArrayList<>();
			for (PlayerStateData state : update.getPlayerStatesDatas()) {
				if (state != null) {
					validStates.add(state);
				}
			}

			if (validStates.size() >= 2) {
				validStates.sort(Comparator.comparingInt(PlayerStateData::getplayerId));
				this.Player1State = validStates.get(0);
				this.Player2State = validStates.get(1);
				p1Changed = true;
				p2Changed = true;
			} else if (validStates.size() == 1) {
				PlayerStateData single = validStates.get(0);
				if (this.Player1State != null && this.Player1State.getplayerId() == single.getplayerId()) {
					this.Player1State = single;
					p1Changed = true;
				} else if (this.Player2State != null && this.Player2State.getplayerId() == single.getplayerId()) {
					this.Player2State = single;
					p2Changed = true;
				} else if (this.Player1State == null) {
					this.Player1State = single;
					p1Changed = true;
				} else if (this.Player2State == null) {
					if (single.getplayerId() < this.Player1State.getplayerId()) {
						this.Player2State = this.Player1State;
						this.Player1State = single;
						p1Changed = true;
						p2Changed = true;
					} else {
						this.Player2State = single;
						p2Changed = true;
					}
				}
			}
		}

		// 떨어지는 일반 단어 목록 반영
		if (update.getPlayer1FallingWords() != null) {
			this.player1FallingWords = new ArrayList<>(update.getPlayer1FallingWords());
			wordsChanged = true;
		}
		if (update.getPlayer2FallingWords() != null) {
			this.player2FallingWords = new ArrayList<>(update.getPlayer2FallingWords());
			wordsChanged = true;
		}

		// 중앙 제시 아이템 단어 반영 (null이 전달되면 아이템 제거로 처리)
		AttackItemWord newCenterWord = update.getAttackItemWord();
		if (!Objects.equals(this.centerItemWord, newCenterWord)) {
			this.centerItemWord = newCenterWord;
			wordsChanged = true;
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

	// 중앙 아이템 제거 패킷을 별도로 받을 경우 직접 비우는 메서드
	public void clearCenterItemWord() {
		if (this.centerItemWord != null) {
			this.centerItemWord = null;
			notifyWordsUpdated();
			notifyGameStateChanged();
		}
	}

	// 전달받은 리스너를 내부 리스너 목록에 추가하기
	public void addListener(GameModelListener listener) {
		if (listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	// 상태 업데이트 알림
	public void notifyWordsUpdated() {
		if (listeners == null || listeners.isEmpty()) {
			return;
		}
		for (GameModelListener listener : new ArrayList<>(listeners)) {
			listener.onWordsUpdated(this);
		}
	}

	public void notifyPlayer1StateUpdated() {
		if (listeners == null || listeners.isEmpty()) {
			return;
		}
		for (GameModelListener listener : new ArrayList<>(listeners)) {
			listener.onPlayer1StateUpdated(this);
		}
	}

	public void notifyPlayer2StateUpdated() {
		if (listeners == null || listeners.isEmpty()) {
			return;
		}
		for (GameModelListener listener : new ArrayList<>(listeners)) {
			listener.onPlayer2StateUpdated(this);
		}
	}

	public void notifyGameStateChanged() {
		if (listeners == null || listeners.isEmpty()) {
			return;
		}
		for (GameModelListener listener : new ArrayList<>(listeners)) {
			listener.onGameStateChanged(this);
		}
	}

	// getter / setter
	public int getLocalPlayerId() {
		return localPlayerId;
	}

	public void setLocalPlayerId(int localPlayerId) {
		this.localPlayerId = localPlayerId;
	}

	public PlayerStateData getPlayer1state() {
		return Player1State;
	}

	public PlayerStateData getPlayer2state() {
		return Player2State;
	}

	public List<PlayerStateData> getPlayerStateList() {
		List<PlayerStateData> list = new ArrayList<>();
		if (Player1State != null) {
			list.add(Player1State);
		}
		if (Player2State != null) {
			list.add(Player2State);
		}
		return list;
	}

	public List<ClientFallingWord> getPlayer1FallingWords() {
	    return player1FallingWords;
	}

	public List<ClientFallingWord> getPlayer2FallingWords() {
	    return player2FallingWords;
	}

	public AttackItemWord getCenterItemWord() {
		return centerItemWord;
	}

	public List<String> getLog() {
		return logs;
	}
}