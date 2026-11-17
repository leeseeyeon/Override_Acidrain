package client.lobby;

import client.network.*;
import common.message.*;
import common.message.lobby.*;

/*
 참가자 역할: "준비" 버튼 → READY 전송
 테마 편집 불가
 준비 후 "기다리는 중…"으로 버튼 잠김
 */
public class GuestRole implements LobbyRole {

    @Override
    public String getActionButtonText(LobbyModel model) {
        if (model.isCountingDown()) {
            return "곧 시작합니다!";
        }
        return model.isGuestReady() ? "시작하길 기다리는 중…" : "준비";
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
        conn.send(JsonUtil.encode(new ReadyMessage(true)));   // ❓ send()가 String을 받는지 확인
    }

    @Override
    public boolean canEditOptions() {
        return false;
    }
}