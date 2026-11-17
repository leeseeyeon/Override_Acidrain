package client.start;

import javax.swing.*;

import client.network.*;
import client.ui.*; //screennavigator 사용
import common.message.*;
import common.message.lobby.*;

/**
 * 시작 화면 Controller
 *
 * 흐름: 닉네임 검사 → 접속 → JOIN 전송 → (서버) JOINED 또는 ERROR
 *   - JOINED: 닉네임과 역할을 넘기며 대기실 화면으로 전환 (screenNavigator.showLobbyScreen)
 *   - ERROR : 시작 화면에 에러 표시 (닉네임 중복, 방 꽉 참 등)
 */
public class StartController implements MessageListener {
    private static final String SERVER_IP = "127.0.0.1";   //Railway 주소 확정 후 기입
    private static final int SERVER_PORT = 9999;           //확정 후 기입

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
            return true;   // 닉네임 중복 후 재시도할 때는 같은 연결 재사용 (❓ 서버가 허용하는지 확인)
        }
        try {
            serverConnection.connect(SERVER_IP, SERVER_PORT);
            isConnected = true;
            return true;
        } catch (Exception e) {  
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
            case DUPLICATE_NICKNAME -> "이미 사용 중인 닉네임입니다.";
            case ROOM_FULL          -> "방이 꽉 찼습니다. 잠시 후 다시 시도하세요.";
            case ROOM_NOT_FOUND     -> "방을 찾을 수 없습니다.";
            default                 -> "입장에 실패했습니다.";
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