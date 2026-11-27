package client.lobby;

import javax.swing.*;

import client.lobby.countdown.CountdownListener;
import client.lobby.countdown.GameStartCountdown;
import client.lobby.role.GuestRole;
import client.lobby.role.HostRole;
import client.lobby.role.LobbyRole;
import client.network.*;
import client.ui.*;
import common.message.*;
import common.message.game.*;
import common.message.lobby.*;
import common.type.ErrorCode;
import common.type.Role;
import common.type.Theme;

public class LobbyController implements MessageListener, CountdownListener {
    private final LobbyModel lobbyModel;
    private final LobbyView lobbyView;
    private final ServerConnection serverConnection;
    private final ScreenNavigator screenNavigator;
    private final GameStartCountdown gameStartCountdown;
    private final GameStartCountdown returnToStartCountdown = new GameStartCountdown();

    private LobbyRole myRole = new GuestRole();
    private boolean isInLobby = false;

    private GameStartMessage pendingGameStart;   // GAME_START 정보 (카운트다운이 끝나면 게임 화면에 전달)
    private int myPlayerId;            

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

    //시작 화면에서 넘어올 때 (MainFrame의 showLobbyScreen()이 호출)
    /** 처음 입장: 역할을 정하고 Model을 새 방 상태로 초기화한 뒤 버튼 갱신 */
    public void enterLobby(String myNickname, Role role) {
        isInLobby = true;
        changeMyRole(role);
        lobbyModel.enterRoom(myNickname, role == Role.HOST);
        lobbyModel.addChatMessage(ChatMessage.system("대기실에 입장했습니다."));
        refreshActionButton();
    }

    //게임 결과화면에서 재시작 버튼 클릭, 기존의 대기실로 돌아올 때 (MainFrame의 returnToLobbyScreen()이 호출)
    /** enterLobby()와 달리 상대, 역할, 준비, 채팅을 초기화하지 않음 (게임 중에 받아 둔 방 상태를 그대로 유지) */
    public void returnToLobby() {
        isInLobby = true;
        lobbyModel.addChatMessage(ChatMessage.system("대기실로 돌아왔습니다."));
        refreshActionButton();
    }

    //사용자 입력
    /** 하단 버튼을 눌렀을 때: 버튼을 잠그고, 할 일(방장: 시작 / 참가자: 준비)은 역할에게 맡김 */
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
        if (!isInLobby || content == null || content.isBlank()) {
            return;
        }
        sendToServer(new common.message.lobby.ChatMessage(content.trim()));
    }

    // 서버 메시지 (MessageListener)
    /** 수신 쓰레드에서 호출됨 → Swing 쓰레드로 넘겨서 처리 */
    @Override
    public void update(String json) {
        SwingUtilities.invokeLater(() -> handleServerMessage(json));
    }

    /** 서버 쪽에서 연결이 끊겼을 때 받기 쓰레드에서 호출됨 → Swing 쓰레드로 넘겨서 처리 */
    @Override
    public void onDisconnected() {
        SwingUtilities.invokeLater(this::handleDisconnected);
    }

    /** 대기실에서 서버 연결이 끊겼을 때: 안내 → 3, 2, 1 → 시작 화면 */
    private void handleDisconnected() {
        if (!isInLobby) {
            return;
        }
        isInLobby = false;              //이후 서버 메시지와 입력 무시
        gameStartCountdown.cancel();    //게임 시작 카운트다운 중이었다면 취소

        lobbyView.updateActionButton("연결 끊김", false);
        lobbyView.setOptionEditable(false);
        lobbyModel.addChatMessage(ChatMessage.system("서버와의 연결이 끊겼습니다. 시작 화면으로 돌아갑니다."));

        returnToStartCountdown.start(new CountdownListener() {
            @Override
            public void onCountdownTicked(int remainingSeconds) {
                lobbyModel.addChatMessage(ChatMessage.system(remainingSeconds + "..."));
            }

            @Override
            public void onCountdownFinished() { //시작화면으로 복귀
                screenNavigator.showStartScreen();
            }
        });
    }

    private void handleServerMessage(String json) {
        Message message = JsonUtil.decodeOrNull(json, "LobbyController"); 
        if (message == null) {
            return;
        }

        //방 상태(상대 입장 및 퇴장, 방장 승계, 상대 준비)는 본게임 화면에 있는 동안에도 반영
        switch (message.getType()) {
            case MessageType.OPPONENT      -> { handleOpponentJoined((OpponentMessage) message); return; }
            case MessageType.OPPONENT_LEFT -> { handleOpponentLeft(); return; }
            case MessageType.ROLE          -> { handleRoleChanged((RoleMessage) message); return; }
            case MessageType.READY         -> { handleGuestReadyChanged((ReadyMessage) message); return; }
            default -> { }
        }

        if (!isInLobby) {
            return;   //나머지는 대기실에 있을 때만 처리 (입장 전, 게임 화면, 연결 끊김 후에는 무시)
        }
        switch (message.getType()) {
            case MessageType.THEME      -> handleThemeChanged((ThemeMessage) message);
            case MessageType.CHAT       -> handleChatReceived((common.message.lobby.ChatMessage) message);
            case MessageType.GAME_START -> handleGameStarted((GameStartMessage) message);
            case MessageType.ERROR      -> handleLobbyError((ErrorMessage) message);
            default -> { /* 대기실과 무관한 메시지는 무시 */ }
        }
    }

    /** 상대 입장: 상대 닉네임 저장, 입장 알림, 버튼 갱신 */
    private void handleOpponentJoined(OpponentMessage opponentMessage) {
        lobbyModel.setOpponentNickname(opponentMessage.getNickname());
        lobbyModel.addChatMessage(ChatMessage.system(opponentMessage.getNickname() + "님이 입장했습니다."));
        refreshActionButton();
    }

    /** 참가자 준비 상태 변경: Model에 반영하고 버튼 갱신 (방장 "시작!" 활성화 여부) */
    private void handleGuestReadyChanged(ReadyMessage readyMessage) {
        lobbyModel.updateGuestReady(readyMessage.isReady());
        refreshActionButton();
    }

    /** 테마 변경: 서버가 확정한 테마를 Model에 반영 (두 사람 화면 모두) */
    private void handleThemeChanged(ThemeMessage themeMessage) {
        lobbyModel.setSelectedTheme(themeMessage.getTheme());
    }

    /** 채팅 수신: 서버용 채팅 메시지를 화면용 ChatMessage로 바꿔 Model에 추가 */
    private void handleChatReceived(common.message.lobby.ChatMessage serverChatMessage) {
        lobbyModel.addChatMessage(new ChatMessage(serverChatMessage.getNickname(), serverChatMessage.getContent()));
    }

    /** 역할 변경(방장 승계): 역할 객체와 버튼 갱신 */
    private void handleRoleChanged(RoleMessage roleMessage) {
        changeMyRole(roleMessage.getRole());
        lobbyModel.changeLocalRole(roleMessage.getRole() == Role.HOST);
        if (roleMessage.getRole() == Role.HOST) {
            lobbyModel.addChatMessage(ChatMessage.system("방장이 되었습니다."));
        }
        refreshActionButton();
    }

    /** 상대 퇴장: 알림, 게임 시작 카운트다운 취소, 상대 정보 지우고 버튼 갱신 */
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

    /** 게임 시작: 게임 정보와 내 id를 보관하고 3, 2, 1 카운트다운 시작 */
    private void handleGameStarted(GameStartMessage gameStartMessage) {
        pendingGameStart = gameStartMessage;
        myPlayerId = findMyPlayerId(gameStartMessage);
        lobbyModel.updateGuestReady(false); 
        lobbyModel.setCountingDown(true);
        refreshActionButton();
        gameStartCountdown.start(this);
    }

    /** 서버는 GAME_START의 player1에 항상 방장을 넣음 → 방장이면 player1, 참가자면 player2의 고유 id get*/
    private int findMyPlayerId(GameStartMessage gameStartMessage) {
        if (lobbyModel.isLocalPlayerHost()) {
            return gameStartMessage.getPlayer1().getPlayerId();
        }
        return gameStartMessage.getPlayer2().getPlayerId();
    }

    /** 서버 에러: 방장 전용 동작이면 안내하고, 잠갔던 버튼 복구 */
    private void handleLobbyError(ErrorMessage errorMessage) {
        if (errorMessage.getErrorCode() == ErrorCode.NOT_HOST) {
            lobbyModel.addChatMessage(ChatMessage.system("방장만 할 수 있는 동작입니다."));
        }
        refreshActionButton();   // "처리 중…"으로 잠갔던 버튼 복구
    }

    //카운트다운 (CountdownListener)
    @Override
    public void onCountdownTicked(int remainingSeconds) {
        lobbyModel.addChatMessage(ChatMessage.system(remainingSeconds + "..."));
    }

    /** 카운트다운 끝: 대기실을 떠나 보관해 둔 게임 정보와 플레이어 고유 id로 게임 화면 전환 */
    @Override
    public void onCountdownFinished() {
        isInLobby = false;
        lobbyModel.setCountingDown(false);
        lobbyModel.updateGuestReady(false);  
        screenNavigator.showGameScreen(pendingGameStart, myPlayerId);   // 게임 시작 정보 + 플레이어 id 전달
    }


    /** 역할에 맞는 역할 객체(HostRole / GuestRole)로 바꾸고 옵션 수정 가능 여부 반영 */
    private void changeMyRole(Role role) {
        myRole = (role == Role.HOST) ? new HostRole() : new GuestRole();
        lobbyView.setOptionEditable(myRole.canEditOptions());
    }

    private void refreshActionButton() {
        lobbyView.updateActionButton(
                myRole.getActionButtonText(lobbyModel),
                myRole.isActionButtonEnabled(lobbyModel));
    }

    private void sendToServer(Message message) {
        serverConnection.send(message); 
    }
}