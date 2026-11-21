package client.start;

import javax.swing.*;

import client.network.*;
import client.ui.*;
import common.NetworkConfig;
import common.message.*;
import common.message.lobby.*;

/**
 * 시작 화면 StartController
 *
 * 흐름: 닉네임 검사 → 접속 → JOIN 전송 → (서버) JOINED 또는 ERROR
 *   - JOINED: 닉네임과 역할을 넘기며 대기실 화면으로 전환 (screenNavigator.showLobbyScreen)
 *   - ERROR : 시작 화면에 에러 표시
 * ℹ 이 객체는 connect() 전에 serverConnection.addListener(this)로 등록되어야 함
 */
public class StartController implements MessageListener {
    private final StartView startView;
    private final NicknameValidator nicknameValidator;
    private final ServerConnection serverConnection;
    private final ScreenNavigator screenNavigator;

    private boolean isConnected = false;
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
            startView.showErrorMessage(nicknameWarning);   // 걸린 규칙에 맞는 경고
            return;
        }
        startView.showErrorMessage(" ");

        if (!connectIfNeeded()) {
            startView.showErrorMessage("서버에 연결할 수 없습니다.");
            return;
        }

        requestedNickname = nickname;
        isWaitingForJoinResult = true;
        sendToServer(new JoinMessage(nickname));
    }

    private boolean connectIfNeeded() {
        if (isConnected) {
            return true;   // 입장 실패 후 재시도할 때는 같은 연결 재사용 (서버는 연결을 끊지 않음)
        }
        try {
            serverConnection.connect(NetworkConfig.SERVER_IP, NetworkConfig.SERVER_PORT);   // 접속 주소는 common.NetworkConfig에서 관리
            isConnected = true;
            return true;
        } catch (Exception e) {   // 서버가 꺼져 있거나 주소/포트가 틀림
            return false;
        }
    }

    /** 수신 쓰레드에서 호출됨 → Swing 쓰레드로 넘겨서 처리 */
    @Override
    public void update(String json) {
        SwingUtilities.invokeLater(() -> handleServerMessage(json));
    }

    private void handleServerMessage(String json) {
        if (!isWaitingForJoinResult) {
            return;   // 대기실로 넘어간 뒤의 메시지는 LobbyController가 처리
        }
        Message message = decodeOrNull(json);
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

    private Message decodeOrNull(String json) {
        try {
            return JsonUtil.decode(json);
        } catch (RuntimeException e) {   // JsonUtil에 등록되지 않은 type 등
            System.err.println("[StartController] 해석할 수 없는 메시지: " + json);
            return null;
        }
    }
}