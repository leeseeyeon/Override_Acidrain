package client.lobby;

import javax.swing.*;


import client.network.*;
import client.ui.*;
import common.message.*;
import common.message.game.*;
import common.message.lobby.*;

/*
대기실 Controller
방장/참가자 차이는 myRole(LobbyRole)에게 맡김
*/
public class LobbyController implements MessageListener, CountdownListener {
    private final LobbyModel lobbyModel;
    private final LobbyView lobbyView;
    private final ServerConnection serverConnection;
    private final ScreenNavigator screenNavigator;
    private final GameStartCountdown gameStartCountdown;
    private final GameStartCountdown returnToStartCountdown = new GameStartCountdown();   // 연결 끊김 후 3, 2, 1 (게임 시작용과 별개)

    private LobbyRole myRole = new GuestRole();
    private boolean isInLobby = false;

    private GameStartMessage pendingGameStart;   // GAME_START 정보 (카운트다운이 끝나면 게임 화면에 넘김)
    private int myPlayerId;                      // 게임에서 쓸 내 고유 id (GAME_START에 따로 없어서 직접 계산)

    public LobbyController(LobbyModel lobbyModel, LobbyView lobbyView, ServerConnection serverConnection,
                           ScreenNavigator screenNavigator, GameStartCountdown gameStartCountdown) {
        this.lobbyModel = lobbyModel;
        this.lobbyView = lobbyView;
        this.serverConnection = serverConnection;
        this.screenNavigator = screenNavigator;
        this.gameStartCountdown = gameStartCountdown;

        // Model → View (Observer)
        lobbyModel.addListener(lobbyView);

        // View → Controller
        lobbyView.setActionButtonListener(e -> onLobbyActionButtonClicked());
        lobbyView.setThemeSelectListener(this::onThemeSelected);
        lobbyView.setChatEnterListener(e -> onChatEntered(e.getActionCommand()));
    }

    // ===== 시작 화면에서 넘어올 때 (MainFrame의 showLobbyScreen()이 호출) =====

    public void enterLobby(String myNickname, Role role) {
        isInLobby = true;
        changeMyRole(role);
        lobbyModel.enterRoom(myNickname, role == Role.HOST);
        lobbyModel.addChatMessage(ChatMessage.system("대기실에 입장했습니다."));
        refreshActionButton();
    }

    // ===== 사용자 입력 =====

    public void onLobbyActionButtonClicked() {
        lobbyView.updateActionButton("처리 중…", false);   // 서버 응답이 올 때까지 연타 방지
        myRole.handleActionButtonClick(serverConnection, lobbyModel);
    }

    public void onThemeSelected(Theme theme) {
        if (!myRole.canEditOptions() || theme == lobbyModel.getSelectedTheme()) {
            return;
        }
        sendToServer(new ThemeMessage(theme));   // 서버에게 선택한 테마 전달.
    }

    public void onChatEntered(String content) {
        if (!isInLobby || content == null || content.isBlank()) {   // 연결이 끊긴 뒤에는 보내지 않음
            return;
        }
        sendToServer(new common.message.lobby.ChatMessage(content.trim()));   // 내 채팅도 서버가 돌려줄 때 표시
    }

    // ===== 서버 메시지 (MessageListener) =====

    /* 수신 쓰레드에서 호출됨 → Swing 쓰레드로 넘겨서 처리 */
    @Override
    public void update(String json) {
        SwingUtilities.invokeLater(() -> handleServerMessage(json));
    }

    /* 서버 쪽에서 연결이 끊겼을 때 받기 쓰레드에서 호출됨 → Swing 쓰레드로 넘겨서 처리 */
    @Override
    public void onDisconnected() {
        SwingUtilities.invokeLater(this::handleDisconnected);
    }

    /* 대기실에서 끊기면: 안내 → 3, 2, 1 → 시작 화면 */
    private void handleDisconnected() {
        if (!isInLobby) {
            return;   // 시작 화면, 게임 화면에서는 그 화면이 처리
        }
        isInLobby = false;              // 이후 서버 메시지와 입력 무시
        gameStartCountdown.cancel();    // 게임 시작 카운트다운 중이었다면 취소

        lobbyView.updateActionButton("연결 끊김", false);
        lobbyView.setOptionEditable(false);
        lobbyModel.addChatMessage(ChatMessage.system("서버와의 연결이 끊겼습니다. 시작 화면으로 돌아갑니다."));

        returnToStartCountdown.start(new CountdownListener() {
            @Override
            public void onCountdownTicked(int remainingSeconds) {
                lobbyModel.addChatMessage(ChatMessage.system(remainingSeconds + "..."));
            }

            @Override
            public void onCountdownFinished() {
                screenNavigator.showStartScreen();
            }
        });
    }

    private void handleServerMessage(String json) {
        if (!isInLobby) {
            return;   // 입장 전(시작 화면) 또는 게임 화면으로 넘어간 뒤의 메시지는 무시
        }
        Message message = decodeOrNull(json);
        if (message == null) {
            return;
        }

        switch (message.getType()) {
            case MessageType.OPPONENT      -> handleOpponentJoined((OpponentMessage) message);
            case MessageType.READY         -> handleGuestReadyChanged((ReadyMessage) message);
            case MessageType.THEME         -> handleThemeChanged((ThemeMessage) message);
            case MessageType.CHAT          -> handleChatReceived((common.message.lobby.ChatMessage) message);
            case MessageType.ROLE          -> handleRoleChanged((RoleMessage) message);
            case MessageType.OPPONENT_LEFT -> handleOpponentLeft();
            case MessageType.GAME_START    -> handleGameStarted((GameStartMessage) message);
            case MessageType.ERROR         -> handleLobbyError((ErrorMessage) message);
            default -> { /* 대기실과 무관한 메시지는 무시 */ }
        }
    }

    private void handleOpponentJoined(OpponentMessage opponentMessage) {
        lobbyModel.setOpponentNickname(opponentMessage.getNickname());
        lobbyModel.addChatMessage(ChatMessage.system(opponentMessage.getNickname() + "님이 입장했습니다."));
        refreshActionButton();
    }

    private void handleGuestReadyChanged(ReadyMessage readyMessage) {
        lobbyModel.updateGuestReady(readyMessage.isReady());
        refreshActionButton();
    }

    private void handleThemeChanged(ThemeMessage themeMessage) {
        lobbyModel.setSelectedTheme(themeMessage.getTheme());
    }

    private void handleChatReceived(common.message.lobby.ChatMessage serverChatMessage) {
        lobbyModel.addChatMessage(new ChatMessage(serverChatMessage.getNickname(), serverChatMessage.getContent()));
    }

    private void handleRoleChanged(RoleMessage roleMessage) {
        changeMyRole(roleMessage.getRole());
        lobbyModel.changeLocalRole(roleMessage.getRole() == Role.HOST);
        if (roleMessage.getRole() == Role.HOST) {
            lobbyModel.addChatMessage(ChatMessage.system("방장이 되었습니다."));
        }
        refreshActionButton();
    }

    private void handleOpponentLeft() {
        if (lobbyModel.isCountingDown()) {
            lobbyModel.addChatMessage(ChatMessage.system("상대가 나가서 게임 시작이 취소되었습니다."));
        } else {
            lobbyModel.addChatMessage(ChatMessage.system("상대가 나갔습니다."));
        }
        gameStartCountdown.cancel();
        lobbyModel.clearOpponent();
        refreshActionButton();
    }

    private void handleGameStarted(GameStartMessage gameStartMessage) {
        pendingGameStart = gameStartMessage;
        myPlayerId = findMyPlayerId(gameStartMessage);
        lobbyModel.setCountingDown(true);
        refreshActionButton();
        gameStartCountdown.start(this);
    }

    /* HOST면 두 id 중 작은 번호, GUEST면 큰 번호 */
    private int findMyPlayerId(GameStartMessage gameStartMessage) {
        int player1Id = gameStartMessage.getPlayer1().getPlayerId();
        int player2Id = gameStartMessage.getPlayer2().getPlayerId();
        if (lobbyModel.isLocalPlayerHost()) {
            return Math.min(player1Id, player2Id);   // 방장: 작은 번호
        }
        return Math.max(player1Id, player2Id);       // 게스트: 큰 번호
    }

    private void handleLobbyError(ErrorMessage errorMessage) {
        if (errorMessage.getErrorCode() == ErrorCode.NOT_HOST) {
            lobbyModel.addChatMessage(ChatMessage.system("방장만 할 수 있는 동작입니다."));
        }
        refreshActionButton();   // "처리 중…"으로 잠갔던 버튼 복구
    }

    // ===== 카운트다운 (CountdownListener) =====

    @Override
    public void onCountdownTicked(int remainingSeconds) {
        lobbyModel.addChatMessage(ChatMessage.system(remainingSeconds + "..."));
    }

    @Override
    public void onCountdownFinished() {
        isInLobby = false;
        lobbyModel.setCountingDown(false);
        screenNavigator.showGameScreen(pendingGameStart, myPlayerId);   // 게임 시작 정보 + 플레이어 고유 id 전달   
    }


    private void changeMyRole(Role role) {
        myRole = (role == Role.HOST) ? new HostRole() : new GuestRole();
        lobbyView.setOptionEditable(myRole.canEditOptions());
    }

    /* Model 값이 버튼에 영향을 줄 때마다 직접 호출 (Model을 바꾸는 코드를 추가하면 여기 호출도 잊지 말 것) */
    private void refreshActionButton() {
        lobbyView.updateActionButton(
                myRole.getActionButtonText(lobbyModel),
                myRole.isActionButtonEnabled(lobbyModel));
    }

    private void sendToServer(Message message) {
        serverConnection.send(message);   // ServerConnection이 JSON으로 바꿔서 보냄
    }

    private Message decodeOrNull(String json) {
        try {
            return JsonUtil.decode(json);
        } catch (RuntimeException e) {   // JsonUtil에 등록되지 않은 type 등
            System.err.println("[LobbyController] 해석할 수 없는 메시지: " + json);
            return null;
        }
    }
}