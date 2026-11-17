package client.lobby;

import client.network.*;
import common.message.*;
import common.message.lobby.*;

/**
 * 방장 역할: 참가자가 준비하면 "시작!" 버튼 활성화
 *  START 전송, 옵션(테마) 편집 가능
 */
public class HostRole implements LobbyRole {

    @Override
    public String getActionButtonText(LobbyModel model) {
        if (model.isCountingDown()) {
            return "게임을 시작합니다...";
        }
        if (model.getGuestNickname() == null) {
            return "상대를 기다리는 중...";
        }
        return model.isGuestReady() ? "게임 시작!" : "(" + model.getGuestNickname() + ") 준비 중…";
    }

    @Override
    public boolean isActionButtonEnabled(LobbyModel model) {
        return model.getGuestNickname() != null && model.isGuestReady() && !model.isCountingDown();
    }

    @Override
    public void handleActionButtonClick(ServerConnection conn, LobbyModel model) {
        if (!isActionButtonEnabled(model)) {
            return;
        }
        conn.send(SignalMessage.start());   // ServerConnection이 JSON으로 바꿔서 보냄
    }

    @Override
    public boolean canEditOptions() {
        return true;
    }
}