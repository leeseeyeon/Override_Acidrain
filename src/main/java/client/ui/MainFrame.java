//MainFrame
package client.ui;

import java.awt.*;

import javax.swing.*;

import client.lobby.*;
import client.network.*;
import client.start.*;
import common.message.*;

/**
 * prototype (전원 공유!)
 * 
 * 클라이언트 메인 창으로 프로그램 시작점 입니다.
 * 창은 하나만(MainFrame) 두고, 여기서 시작 화면 → 대기실 → 게임 화면을 CardLayout으로 갈아 끼우는 방식
 *
 * 1. 객체 조립: ServerConnection 1개를 만들어 두 Controller가 같이 쓰게 함
 * 2. 리스너 등록: connect() 전에 StartController, LobbyController를 등록
 * 3. 화면 전환: ScreenNavigator 구현 (화면을 넘길 때 필요한 데이터도 같이 전달)
 */
public class MainFrame extends JFrame implements ScreenNavigator {
    private static final String START_SCREEN = "START";
    private static final String LOBBY_SCREEN = "LOBBY";
    private static final String GAME_SCREEN = "GAME";

    private final CardLayout screenCardLayout = new CardLayout();
    private final JPanel screenContainer = new JPanel(screenCardLayout);

    private final LobbyController lobbyController;

    public MainFrame() {
        setTitle("산성비");
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

        // 4. 서버 메시지 리스너 등록
        serverConnection.addListener(startController);
        serverConnection.addListener(lobbyController);

        // 5. 화면 카드 등록
        screenContainer.add(startView, START_SCREEN);
        screenContainer.add(lobbyView, LOBBY_SCREEN);
        screenContainer.add(createGameScreenPlaceholder(), GAME_SCREEN);   // TODO: 채영님 게임 화면 구현 후 수정
        setContentPane(screenContainer);

        pack();
        setLocationRelativeTo(null);   // 화면 가운데에 띄움
        showStartScreen();
    }

    /** 게임 화면 구현전까지 자리만 잡아 두기 위한 임시 패널 */
    private JPanel createGameScreenPlaceholder() {
        JPanel gameScreenPlaceholder = new JPanel(new BorderLayout());
        gameScreenPlaceholder.setPreferredSize(new Dimension(1000, 760));
        gameScreenPlaceholder.add(new JLabel("게임 화면", SwingConstants.CENTER), BorderLayout.CENTER);
        return gameScreenPlaceholder;
    }

    // ===== ScreenNavigator 구현 =====

    @Override
    public void showStartScreen() {
        screenCardLayout.show(screenContainer, START_SCREEN);
    }

    @Override
    public void showLobbyScreen(String myNickname, Role myRole) {
        lobbyController.enterLobby(myNickname, myRole);   // 대기실에 데이터 전달
        screenCardLayout.show(screenContainer, LOBBY_SCREEN);
    }

    @Override
    public void showGameScreen() {
        screenCardLayout.show(screenContainer, GAME_SCREEN);
    }

    // ===== 프로그램 시작 =====

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}