package client.lobby;

import java.util.*;

import common.message.*;

/*
대기실 상태 (Model)
 */
public class LobbyModel {
    private String myNickname;
    private boolean isLocalPlayerHost;
    private String hostNickname;
    private String guestNickname;          // 참가자가 아직 없으면 null
    private boolean isGuestReady;
    private Theme selectedTheme = Theme.BASIC;
    private boolean isCountingDown;
    private final List<ChatMessage> chatMessages = new ArrayList<>();
    private final List<LobbyModelListener> listeners = new ArrayList<>();

    public void addListener(LobbyModelListener listener) {
        listeners.add(listener);
    }

    // ===== 상태 변경 (서버 메시지 하나당 하나씩) =====

    /* JOINED: 방에 처음 들어왔을 때 */
    public void enterRoom(String myNickname, boolean isHost) {
        this.myNickname = myNickname;
        this.isLocalPlayerHost = isHost;
        this.hostNickname = isHost ? myNickname : null;
        this.guestNickname = isHost ? null : myNickname;
        this.isGuestReady = false;
        this.isCountingDown = false;
        this.selectedTheme = Theme.BASIC;
        this.chatMessages.clear();
        notifyLobbyChanged();
    }

    /* OPPONENT: 상대 닉네임을 받았을 때 */
    public void setOpponentNickname(String opponentNickname) {
        if (isLocalPlayerHost) {
            guestNickname = opponentNickname;
        } else {
            hostNickname = opponentNickname;
        }
        notifyLobbyChanged();
    }

    /* OPPONENT_LEFT: 상대가 나갔을 때 */
    public void clearOpponent() {
        if (isLocalPlayerHost) {
            guestNickname = null;
        } else {
            hostNickname = null;
        }
        isGuestReady = false;
        isCountingDown = false;
        notifyLobbyChanged();
    }

    /* ROLE: 내 역할이 바뀌었을 때 (예: 방장이 나가서 내가 방장이 됨) */
    public void changeLocalRole(boolean isHost) {
        if (isLocalPlayerHost == isHost) {
            return;
        }
        String opponentNickname = isLocalPlayerHost ? guestNickname : hostNickname;
        isLocalPlayerHost = isHost;
        hostNickname = isHost ? myNickname : opponentNickname;
        guestNickname = isHost ? opponentNickname : myNickname;
        isGuestReady = false;
        notifyLobbyChanged();
    }

    /* READY: 참가자 준비 상태 */
    public void updateGuestReady(boolean isReady) {
        this.isGuestReady = isReady;
        notifyLobbyChanged();
    }

    /* THEME: 방장이 고른 테마 */
    public void setSelectedTheme(Theme theme) {
        if (theme == null) {
            return;
        }
        this.selectedTheme = theme;
        notifyLobbyChanged();
    }

    /* GAME_START 후 카운트다운 중인지 */
    public void setCountingDown(boolean isCountingDown) {
        this.isCountingDown = isCountingDown;
        notifyLobbyChanged();
    }

    /* CHAT, 시스템 안내 */
    public void addChatMessage(ChatMessage message) {
        chatMessages.add(message);
        notifyLobbyChanged();
    }

    // ===== 조회 =====

    public boolean isLocalPlayerHost() {
        return isLocalPlayerHost;
    }

    public String getHostNickname() {
        return hostNickname;
    }

    public String getGuestNickname() {
        return guestNickname;
    }

    public boolean isGuestReady() {
        return isGuestReady;
    }

    public Theme getSelectedTheme() {
        return selectedTheme;
    }

    public boolean isCountingDown() {
        return isCountingDown;
    }

    public List<ChatMessage> getChatMessages() {
        return Collections.unmodifiableList(chatMessages);
    }

    private void notifyLobbyChanged() {
        for (LobbyModelListener listener : listeners) {
            listener.onLobbyChanged(this);
        }
    }
}