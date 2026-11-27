package client.lobby.role;

import client.lobby.LobbyModel;
import client.network.*;
import common.message.lobby.*;

/**
 * 게스트 역할: "준비 됐나요?" 버튼 클릭 → READY 전송
 * 옵션(테마) 선택 불가
 */
public class GuestRole implements LobbyRole {

    @Override
    public String getActionButtonText(LobbyModel model) {
        if (model.isCountingDown()) {
            return "게임을 시작합니다...";
        }
        return model.isGuestReady() ? "시작하길 기다리는 중..." : "준비 됐나요?";
    }

    @Override
    public boolean isActionButtonEnabled(LobbyModel model) {
        return !model.isGuestReady() && !model.isCountingDown();
    }

    @Override
    public void handleActionButtonClick(ServerConnection conn, LobbyModel model) {
        if (!isActionButtonEnabled(model)) {
            return;
        }
        conn.send(new ReadyMessage(true)); 
    }

    @Override
    public boolean canEditOptions() {
        return false;
    }
}