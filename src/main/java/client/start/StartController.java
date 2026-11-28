package client.start;

import javax.swing.*;

import client.network.*;
import client.ui.*;
import common.config.NetworkConfig;
import common.message.*;
import common.message.lobby.*;
import common.type.ErrorCode;

public class StartController implements MessageListener {
    private final StartView startView;
    private final NicknameValidator nicknameValidator;
    private final ServerConnection serverConnection;
    private final ScreenNavigator screenNavigator;

    private boolean isWaitingForJoinResult = false; 
    private String requestedNickname;                 

    public StartController(StartView startView, NicknameValidator nicknameValidator,
                           ServerConnection serverConnection, ScreenNavigator screenNavigator) {
        this.startView = startView;
        this.nicknameValidator = nicknameValidator;
        this.serverConnection = serverConnection;
        this.screenNavigator = screenNavigator;

        startView.setStartButtonListener(e -> onGameStartButtonClicked());
    }

    /** "게임 입장" 버튼을 눌렀을 때: 닉네임 검사 → 서버 연결 → JOIN 전송 */
    public void onGameStartButtonClicked() {
        if (isWaitingForJoinResult) {
            return;
        }
        String nickname = startView.getEnteredNickname();
        String nicknameWarning = nicknameValidator.findWarningMessage(nickname);
        if (nicknameWarning != null) { //닉네임 규칙 위반 시 
            startView.showErrorMessage(nicknameWarning);
            return;
        }
        startView.showErrorMessage(" ");

        if (!connectToServer()) { //서버 연결 실패 시 
            startView.showErrorMessage("서버에 연결할 수 없습니다.");
            return;
        }

        requestedNickname = nickname;
        isWaitingForJoinResult = true;
        sendToServer(new JoinMessage(nickname));
    }

    
    /** 이전 서버 연결이 남아 있으면 정리하고 새로 연결 */
    private boolean connectToServer() {
        serverConnection.disconnect(); 
        try {
            serverConnection.connect(NetworkConfig.SERVER_IP, NetworkConfig.SERVER_PORT); 
            return true;
        } catch (Exception e) {  //서버가 꺼져 있거나 주소, 포트가 틀린경우
            return false;
        }
    }

    @Override
    public void update(String json) {
        SwingUtilities.invokeLater(() -> handleServerMessage(json));
    }

    @Override
    public void onDisconnected() {
        SwingUtilities.invokeLater(this::handleDisconnected);
    }

    /** 다른 화면(대기실 등)에 있을 때 끊겨도 시작화면이 알고있도록. → 시작 화면으로 '서버 끊김' 안내 */
    private void handleDisconnected() {
        isWaitingForJoinResult = false;   
        startView.showErrorMessage("서버와의 연결이 끊겼습니다. 다시 시도하세요.");
    }

    /** 서버 메시지를 해석해서 JOINED / ERROR만 처리 (입장 응답을 기다릴 때만) */
    private void handleServerMessage(String json) {
        if (!isWaitingForJoinResult) {
            return;  
        }
        Message message = JsonUtil.decodeOrNull(json, "StartController");
        if (message == null) {
            return;
        }

        switch (message.getType()) {
            case MessageType.JOINED -> handleJoinSucceeded((JoinedMessage) message);
            case MessageType.ERROR  -> handleJoinFailed((ErrorMessage) message);
            default -> { /* 시작 화면과 무관한 메시지는 무시 */ }
        }
    }

    /** 입장 성공: 내 닉네임과 서버가 정해 준 역할(방장/참가자)로 대기실 화면 전환 */
    private void handleJoinSucceeded(JoinedMessage joinedMessage) {
        isWaitingForJoinResult = false;
        screenNavigator.showLobbyScreen(requestedNickname, joinedMessage.getRole());
    }

    /** 입장 실패: 에러 코드를 문구로 바꿔 시작 화면에 표시 */
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
        serverConnection.send(message); 
    }
}