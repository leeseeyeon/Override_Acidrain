//ScreenNavigator
package client.ui;

import common.message.*;

/**
 * prototype (전원 공유!)
 * 
 * 화면을 넘길 때 그 화면에 필요한 데이터를 같이 넘김
 * 실제 구현은 메인 창(MainFrame, CardLayout)에서 진행
 * showGameScreen()에 넘길 데이터는 채영님 구현 후 채울 예정
 */
public interface ScreenNavigator {
	//시작화면
    void showStartScreen();

    //대기실
    void showLobbyScreen(String myNickname, Role myRole);

    //게임 화면
    void showGameScreen();
    
    //게임 결과 화면
} 