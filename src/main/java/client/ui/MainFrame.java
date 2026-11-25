//MainFrame
package client.ui;

import java.awt.*;

import javax.swing.*;

import client.game.GameClientModel;
import client.game.GameController;
import client.game.GameOverView;
import client.game.GameView;
import client.lobby.*;
import client.network.*;
import client.start.*;
import common.message.*;
import common.message.game.GameStartMessage;

/**
 * prototype (전원 공유!)
 *
 * 클라이언트 메인 창으로 프로그램 시작점 입니다.
 * 창은 하나만(MainFrame) 두고, 여기서 시작 화면 → 대기실 → 게임 화면을 CardLayout으로 갈아 끼우는 방식
 *
 * 1. 객체 조립: ServerConnection 1개를 만들어 세 Controller(Start, Lobby, Game)가 같이 쓰게 함
 * 2. 리스너 등록: connect() 전에 StartController, LobbyController를 등록
 *    (GameController는 생성자에서 직접 등록함)
 * 3. 화면 전환: ScreenNavigator 구현 (화면을 넘길 때 필요한 데이터도 같이 전달)
 */
public class MainFrame extends JFrame implements ScreenNavigator {
    private static final String START_SCREEN = "START";
    private static final String LOBBY_SCREEN = "LOBBY";
    private static final String GAME_SCREEN = "GAME";

    private final CardLayout screenCardLayout = new CardLayout();
    private final JPanel screenContainer = new JPanel(screenCardLayout);

    private final LobbyController lobbyController;
    private final GameController gameController;

    public MainFrame() {
        setTitle("대머리를 지켜라!"); //제목 표시줄
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);   // 📌 1000 x 760 고정 배치. 수정 가능성O

        // 1. 서버 연결: 클라이언트 전체에서 1개만 만들어 같이 사용
        ServerConnection serverConnection = new ServerConnection();

        // 2. 대기실
        LobbyModel lobbyModel = new LobbyModel();
        LobbyView lobbyView = new LobbyView();
        lobbyController = new LobbyController(lobbyModel, lobbyView, serverConnection, this, new GameStartCountdown());

        // 3. 시작 화면
        StartView startView = new StartView();
        StartController startController = new StartController(startView, new NicknameValidator(), serverConnection, this);

        // 4. 게임 화면 (GameView, GameClientModel, GameController)
        //    GameController는 생성자에서 serverConnection에 자기 리스너를 직접 등록함
        GameClientModel gameModel = new GameClientModel();
        GameView gameView = new GameView();
        gameController = new GameController(gameModel, gameView, serverConnection);
        gameController.setGameOverView(new GameOverView());         // 결과 창 (별도 JFrame, 게임이 끝나면 GameController가 띄움)
        gameController.setOnReturnToLogin(this::showStartScreen);   // 게임 중 연결 끊김 → 3초 뒤 시작 화면
        // TODO(채영님): GameController에 setOnReturnToLobby(Runnable) 구현 후 아래 주석 해제
        // 게임 중 상대가 퇴장할 경우 → 기존 대기실로 복귀 (방 상태 유지) 
        // gameController.setOnReturnToLobby(this::returnToLobbyScreen);

        // 5. 서버 메시지 리스너 등록 (connect() 전에)
        serverConnection.addListener(startController);
        serverConnection.addListener(lobbyController);

        // 6. 화면 카드 등록
        screenContainer.add(startView, START_SCREEN);
        screenContainer.add(lobbyView, LOBBY_SCREEN);
        screenContainer.add(gameView, GAME_SCREEN);
        setContentPane(screenContainer);

        pack();
        setLocationRelativeTo(null);   // 화면 가운데에 띄움
        showStartScreen();
    }

    // ===== ScreenNavigator 구현 =====

    @Override
    public void showStartScreen() {
        screenCardLayout.show(screenContainer, START_SCREEN);
    }

    @Override
    public void showLobbyScreen(String myNickname, Role myRole) {
        lobbyController.enterLobby(myNickname, myRole);   // 대기실 새로 입장 (상태 초기화)
        screenCardLayout.show(screenContainer, LOBBY_SCREEN);
    }

    @Override
    public void returnToLobbyScreen() {
        lobbyController.returnToLobby();   // 초기화 없이 대기실 다시 열기 (게임 중 받아 둔 방 상태 유지)
        screenCardLayout.show(screenContainer, LOBBY_SCREEN);
    }

    @Override
    public void showGameScreen(GameStartMessage gameStartMessage, int myPlayerId) {
        gameController.setLocalPlayerId(myPlayerId);   // 내 고유 playerId (어느 보드가 나인지)
        // 지금 보드의 id와 닉네임은 서버의 첫 GAME_STATE_UPDATE가 와야 채워짐
        // gameStartMessage(player1, player2, theme)를 미리 넣는 메소드가 생기면 여기서 호출
        // 예: gameController.prepareGame(gameStartMessage, myPlayerId);
        gameController.startClientGameLoop();
        screenCardLayout.show(screenContainer, GAME_SCREEN);
    }

    // ===== 프로그램 시작 =====

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
