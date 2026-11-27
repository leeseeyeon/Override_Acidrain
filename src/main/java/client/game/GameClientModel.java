package client.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import common.dto.AttackItemWord;
import common.dto.ClientFallingWord;
import common.dto.PlayerStateData;
import common.message.game.GameStateUpdateMessage;

//서버에서 전달받은 게임 상태(플레이어, 낙하 단어, 아이템, 로그)를 보관하고 리스너에 통지하는 클라이언트 모델
public class GameClientModel {
	private int localPlayerId = -1;
	private PlayerStateData player1State; // ID가 작은 플레이어
	private PlayerStateData player2State; // ID가 큰 플레이어
	private List<ClientFallingWord> player1FallingWords = new ArrayList<>();
	private List<ClientFallingWord> player2FallingWords = new ArrayList<>();
	private AttackItemWord centerItemWord;
	private List<String> logs = new ArrayList<>();
	private List<GameModelListener> listeners = new ArrayList<>();

	// 서버 상태 업데이트 메시지를 모델에 반영하고 변경된 항목별로 리스너에 통지
	public void applyServerUpdate(GameStateUpdateMessage update) {
		if (update == null) {
			return;
		}

		boolean wordsChanged = false;
		boolean stateChanged = false;

		boolean[] playerChanges = applyPlayerStates(update.getPlayerStatesDatas());
		boolean p1Changed = playerChanges[0];
		boolean p2Changed = playerChanges[1];

		if (update.getPlayer1FallingWords() != null) {
			this.player1FallingWords = new ArrayList<>(update.getPlayer1FallingWords());
			wordsChanged = true;
		}
		if (update.getPlayer2FallingWords() != null) {
			this.player2FallingWords = new ArrayList<>(update.getPlayer2FallingWords());
			wordsChanged = true;
		}

		// 중앙 아이템 단어 반영 (null이 전달되면 아이템 제거로 처리)
		AttackItemWord newCenterWord = update.getAttackItemWord();
		if (!Objects.equals(this.centerItemWord, newCenterWord)) {
			this.centerItemWord = newCenterWord;
			wordsChanged = true;
			stateChanged = true;
		}

		// 서버가 전체 로그 목록을 보내므로 기존 내용과 다를 때만 갱신
		if (update.getLogMessage() != null && !update.getLogMessage().equals(this.logs)) {
			this.logs = new ArrayList<>(update.getLogMessage());
			stateChanged = true;
		}

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

	// 플레이어 ID 오름차순(player1 < player2) 기준으로 상태를 정렬·반영하고 각 변경 여부([p1, p2])를 반환
	private boolean[] applyPlayerStates(List<PlayerStateData> rawStates) {
		boolean p1Changed = false;
		boolean p2Changed = false;

		if (rawStates == null || rawStates.isEmpty()) {
			return new boolean[]{false, false};
		}

		List<PlayerStateData> validStates = new ArrayList<>();
		for (PlayerStateData state : rawStates) {
			if (state != null) {
				validStates.add(state);
			}
		}

		if (validStates.size() >= 2) {
			validStates.sort(Comparator.comparingInt(PlayerStateData::getplayerId));
			this.player1State = validStates.get(0);
			this.player2State = validStates.get(1);
			p1Changed = true;
			p2Changed = true;
		} else if (validStates.size() == 1) {
			PlayerStateData single = validStates.get(0);
			if (this.player1State != null && this.player1State.getplayerId() == single.getplayerId()) {
				this.player1State = single;
				p1Changed = true;
			} else if (this.player2State != null && this.player2State.getplayerId() == single.getplayerId()) {
				this.player2State = single;
				p2Changed = true;
			} else if (this.player1State == null) {
				this.player1State = single;
				p1Changed = true;
			} else if (this.player2State == null) {
				if (single.getplayerId() < this.player1State.getplayerId()) {
					this.player2State = this.player1State;
					this.player1State = single;
					p1Changed = true;
					p2Changed = true;
				} else {
					this.player2State = single;
					p2Changed = true;
				}
			}
		}

		return new boolean[]{p1Changed, p2Changed};
	}

	// 중앙 아이템 제거 패킷을 별도로 수신한 경우 아이템 단어를 직접 비우고 통지
	public void clearCenterItemWord() {
		if (this.centerItemWord != null) {
			this.centerItemWord = null;
			notifyWordsUpdated();
			notifyGameStateChanged();
		}
	}

	// 중복 등록을 방지하며 상태 변경 리스너 추가
	public void addListener(GameModelListener listener) {
		if (listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	// 순회 중 수정 예외를 방지하기 위해 리스너 목록을 복사해 일괄 통지
	private void notifyListeners(Consumer<GameModelListener> action) {
		if (listeners == null || listeners.isEmpty()) {
			return;
		}
		for (GameModelListener listener : new ArrayList<>(listeners)) {
			action.accept(listener);
		}
	}

	public void notifyWordsUpdated() {
		notifyListeners(listener -> listener.onWordsUpdated(this));
	}

	public void notifyPlayer1StateUpdated() {
		notifyListeners(listener -> listener.onPlayer1StateUpdated(this));
	}

	public void notifyPlayer2StateUpdated() {
		notifyListeners(listener -> listener.onPlayer2StateUpdated(this));
	}

	public void notifyGameStateChanged() {
		notifyListeners(listener -> listener.onGameStateChanged(this));
	}

	public int getLocalPlayerId() {
		return localPlayerId;
	}

	public void setLocalPlayerId(int localPlayerId) {
		this.localPlayerId = localPlayerId;
	}

	public PlayerStateData getPlayer1state() {
		return player1State;
	}

	public PlayerStateData getPlayer2state() {
		return player2State;
	}

	public List<PlayerStateData> getPlayerStateList() {
		List<PlayerStateData> list = new ArrayList<>();
		if (player1State != null) {
			list.add(player1State);
		}
		if (player2State != null) {
			list.add(player2State);
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