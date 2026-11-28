package client.ui;

import common.message.game.GameStartMessage;
import common.type.Role;

public interface ScreenNavigator {
    //시작화면
    void showStartScreen();

    //대기실 (처음 입장: 대기실 상태를 새로 초기화)
    void showLobbyScreen(String myNickname, Role myRole);

    //대기실 (게임 후 복귀: 상대, 역할, 채팅을 그대로 유지)
    void returnToLobbyScreen();

    //게임 화면 (GAME_START 정보 + 내 고유 playerId)
    void showGameScreen(GameStartMessage gameStartMessage, int myPlayerId);

}
