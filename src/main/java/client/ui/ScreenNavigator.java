//ScreenNavigator
package client.ui;

import common.message.*;
import common.message.game.GameStartMessage;
import common.type.Role;

/**
 * prototype (전원 공유!)
 *
 * 화면을 넘길 때 그 화면에 필요한 데이터를 같이 넘김
 * 실제 구현은 메인 창(MainFrame, CardLayout)에서 진행
 *
 *    showStartScreen()      : StartController(처음), LobbyController/GameController(연결 끊김 후 복귀)
 *    showLobbyScreen()      : StartController (JOINED 받았을 때, 대기실 새로 입장)
 *    returnToLobbyScreen()  : GameController (게임이 끝나고 같은 방 대기실로)
 *    showGameScreen()       : LobbyController (GAME_START 카운트다운이 끝났을 때)
 */
public interface ScreenNavigator {
    //시작화면
    void showStartScreen();

    //대기실 (처음 입장: 대기실 상태를 새로 초기화)
    void showLobbyScreen(String myNickname, Role myRole);

    //대기실 (게임 후 복귀: 상대, 역할, 채팅을 그대로 유지)
    void returnToLobbyScreen();

    //게임 화면 (GAME_START 정보 + 내 고유 playerId)
    void showGameScreen(GameStartMessage gameStartMessage, int myPlayerId);

    //게임 결과 화면: 지금은 GameController가 GameOverView(별도 창)를 직접 띄움 → 메소드 없음
}
