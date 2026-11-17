package client.lobby;

/**
 * LobbyModel이 바뀌었을 때 알림을 받는 약속 (Observer 패턴)
 */
public interface LobbyModelListener {
    void onLobbyChanged(LobbyModel model);
}