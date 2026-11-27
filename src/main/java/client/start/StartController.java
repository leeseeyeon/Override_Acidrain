package client.start;

import javax.swing.*;

import client.network.*;
import client.ui.*;
import common.config.NetworkConfig;
import common.message.*;
import common.message.lobby.*;
import common.type.ErrorCode;

/**
 * StartController
 *
 * 흐름: 닉네임 검사 → 접속 → JOIN 전송 → (서버) JOINED 또는 ERROR
 *    JOINED 의 경우: 닉네임과 역할을 넘기며 대기실 화면으로 전환
 *    ERROR 의 경우: 시작 화면에 에러 표시
 * 연결: 게임 시작을 누를 때마다 남은 연결을 정리하고 새로 연결 (connectToServer)
 *    🔥🔥서버 쪽에서 끊기면 onDisconnected()로 알림을 받아 시작 화면에 안내
 */
public class StartController implements MessageListener {
    private final StartView startView;
    private final NicknameValidator nicknameValidator;
    private final ServerConnection serverConnection;
    private final ScreenNavigator screenNavigator;

    private boolean isWaitingForJoinResult = false;   // 서버 응답을 기다리는 중 (중복 클릭 방지)
    private String requestedNickname;                 // JOINED에 내 닉네임이 없어서 보낸 값을 기억해 둠

    public StartController(StartView startView, NicknameValidator nicknameValidator,
                           ServerConnection serverConnection, ScreenNavigator screenNavigator) {
        this.startView = startView;
        this.nicknameValidator = nicknameValidator;
        this.serverConnection = serverConnection;
        this.screenNavigator = screenNavigator;

        startView.setStartButtonListener(e -> onGameStartButtonClicked());
    }

    public void onGameStartButtonClicked() {
        if (isWaitingForJoinResult) {
            return;
        }
        String nickname = startView.getEnteredNickname();
        String nicknameWarning = nicknameValidator.findWarningMessage(nickname);
        if (nicknameWarning != null) {
            startView.showErrorMessage(nicknameWarning);   // 걸린 규칙에 맞는 경고를 view 에 넘김
            return;
        }
        startView.showErrorMessage(" ");

        if (!connectToServer()) { //🔥🔥
            startView.showErrorMessage("서버에 연결할 수 없습니다.");
            return;
        }

        requestedNickname = nickname;
        isWaitingForJoinResult = true;
        sendToServer(new JoinMessage(nickname));
    }

    
    /** 🔥🔥이전 연결이 남아 있으면 정리하고 새로 연결. 실패하면 false */
    private boolean connectToServer() {
        serverConnection.disconnect();   // 남아 있는 연결 정리 (이미 닫혔거나 없으면 아무것도 안 함)
        try {
            serverConnection.connect(NetworkConfig.SERVER_IP, NetworkConfig.SERVER_PORT);   // 접속 주소는 NetworkConfig에서 관리
            return true;
        } catch (Exception e) {   // 서버가 꺼져 있거나 주소, 포트가 틀린경우
            return false;
        }
    }

    /** 수신 쓰레드에서 호출됨 → Swing 쓰레드로 넘겨서 처리 */
    @Override
    public void update(String json) {
        SwingUtilities.invokeLater(() -> handleServerMessage(json));
    }

    /**🔥🔥 서버 쪽에서 연결이 끊겼을 때 받기 쓰레드에서 호출됨 → Swing 쓰레드로 넘겨서 처리 */
    @Override
    public void onDisconnected() {
        SwingUtilities.invokeLater(this::handleDisconnected);
    }

    /**🔥🔥 다른 화면(대기실 등)에 있을 때 끊겨도 시작화면이 알고있도록. → 시작 화면으로 '서버 끊김' 안내 */
    private void handleDisconnected() {
        isWaitingForJoinResult = false;   // 응답을 기다리던 중이었다면 다시 누를 수 있게
        startView.showErrorMessage("서버와의 연결이 끊겼습니다. 다시 시도하세요.");
    }

    private void handleServerMessage(String json) {
        if (!isWaitingForJoinResult) {
            return;   // 대기실로 넘어간 뒤의 메시지는 LobbyController가 처리
        }
        Message message = JsonUtil.decodeOrNull(json, "StartController");   // 해석 못 하면 로그 남기고 null
        if (message == null) {
            return;
        }

        switch (message.getType()) {
            case MessageType.JOINED -> handleJoinSucceeded((JoinedMessage) message);
            case MessageType.ERROR  -> handleJoinFailed((ErrorMessage) message);
            default -> { /* 시작 화면과 무관한 메시지는 무시 */ }
        }
    }

    private void handleJoinSucceeded(JoinedMessage joinedMessage) {
        isWaitingForJoinResult = false;
        screenNavigator.showLobbyScreen(requestedNickname, joinedMessage.getRole());
    }

    private void handleJoinFailed(ErrorMessage errorMessage) {
        isWaitingForJoinResult = false;
        startView.showErrorMessage(toErrorText(errorMessage.getErrorCode()));
    }

    private String toErrorText(ErrorCode errorCode) {
        if (errorCode == null) {
            return "입장에 실패했습니다.";
        }
        return switch (errorCode) {
            case ROOM_NOT_FOUND -> "방을 찾을 수 없습니다.";
            default             -> "입장에 실패했습니다.";
        };
    }

    private void sendToServer(Message message) {
        serverConnection.send(message);   // ServerConnection이 JSON으로 바꿔서 보냄
    }
}