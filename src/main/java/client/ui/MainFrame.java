package client.ui;

import java.awt.*;

import javax.swing.*;

import client.game.GameClientModel;
import client.game.GameController;
import client.game.GameView;
import client.lobby.*;
import client.lobby.countdown.GameStartCountdown;
import client.network.*;
import client.result.GameOverView;
import client.start.*;
import common.message.game.GameStartMessage;
import common.type.Role;


public class MainFrame extends JFrame implements ScreenNavigator {
    private static final String START_SCREEN = "START";
    private static final String LOBBY_SCREEN = "LOBBY";
    private static final String GAME_SCREEN = "GAME";

    private final CardLayout screenCardLayout = new CardLayout();
    private final JPanel screenContainer = new JPanel(screenCardLayout);

    private final LobbyController lobbyController;
    private final GameController gameController;

    public MainFrame() {
        setUpFrame();

        //서버 연결: 클라이언트 전체에서 1개만 만들어 같이 사용
        ServerConnection serverConnection = new ServerConnection();

        LobbyView lobbyView = new LobbyView();
        lobbyController = createLobbyController(lobbyView, serverConnection);

        StartView startView = new StartView();
        StartController startController = createStartController(startView, serverConnection);

        GameView gameView = new GameView();
        gameController = createGameController(gameView, serverConnection);

        registerListeners(serverConnection, startController);
        registerScreens(startView, lobbyView, gameView);
        showFrame();
    }

    private void setUpFrame() {
        setTitle("SSS급 타자천재가 키보드를 잡았다"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); 
    }

    /** 대기실 */
    private LobbyController createLobbyController(LobbyView lobbyView, ServerConnection serverConnection) {
        LobbyModel lobbyModel = new LobbyModel();
        return new LobbyController(lobbyModel, lobbyView, serverConnection, this, new GameStartCountdown());
    }

    /** 시작 화면 */
    private StartController createStartController(StartView startView, ServerConnection serverConnection) {
        return new StartController(startView, new NicknameValidator(), serverConnection, this);
    }

    /** 게임 화면 */
    private GameController createGameController(GameView gameView, ServerConnection serverConnection) {
        GameClientModel gameModel = new GameClientModel();
        GameController controller = new GameController(gameModel, gameView, serverConnection);
        controller.setGameOverView(new GameOverView()); 
        controller.setOnReturnToLogin(this::showStartScreen); 
        controller.setOnReturnToLobby(this::returnToLobbyScreen); 
        return controller;
    }

    /** 서버 메시지 리스너 등록 */
    private void registerListeners(ServerConnection serverConnection, StartController startController) {
        serverConnection.addListener(startController);
        serverConnection.addListener(lobbyController);
    }

    /** 화면 카드 등록 */
    private void registerScreens(StartView startView, LobbyView lobbyView, GameView gameView) {
        screenContainer.add(startView, START_SCREEN);
        screenContainer.add(lobbyView, LOBBY_SCREEN);
        screenContainer.add(gameView, GAME_SCREEN);
        setContentPane(screenContainer);
    }

    private void showFrame() {
        pack();
        setLocationRelativeTo(null);
        showStartScreen();
    }

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
        gameController.setLocalPlayerId(myPlayerId);  
        gameController.startClientGameLoop();
        screenCardLayout.show(screenContainer, GAME_SCREEN);
    }

    //프로그램 시작 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}