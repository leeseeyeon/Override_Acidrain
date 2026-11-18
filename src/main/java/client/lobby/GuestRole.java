package client.lobby;

import client.network.*;
import common.message.*;
import common.message.lobby.*;

/**
 * 참가자 역할: "준비" 버튼 → READY 전송, 옵션(테마) 편집 불가
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
        conn.send(new ReadyMessage(true));   // ServerConnection이 JSON으로 바꿔서 보냄
    }

    @Override
    public boolean canEditOptions() {
        return false;
    }
}