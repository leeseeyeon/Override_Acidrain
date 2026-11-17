package client.lobby;

import client.network.*;

/**
 * 대기실에서의 역할별 동작 (Strategy 패턴)
 *
 * ℹ 방장/참가자의 차이(하단 버튼, 옵션 편집)를 if (isHost) 대신 클래스로 분리
 * ℹ 역할은 서버의 ROLE 메시지로 도중에 바뀔 수 있음 → LobbyController가 myRole을 교체
 */
public interface LobbyRole {
    String getActionButtonText(LobbyModel model);

    boolean isActionButtonEnabled(LobbyModel model);

    void handleActionButtonClick(ServerConnection conn, LobbyModel model);

    boolean canEditOptions();
}