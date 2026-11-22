package server.room;

import java.util.concurrent.atomic.AtomicInteger;

import common.message.ErrorCode;
import common.message.ErrorMessage;
import common.message.Message;
import common.message.Role;
import common.message.SignalMessage;
import common.message.Theme;
import common.message.game.PlayerProfile;
import common.message.lobby.*;
import common.message.game.*;
import server.network.ClientHandler;

public class GameRoom {
	private static final AtomicInteger NEXT_GAME_ID = new AtomicInteger(1); // 모든 방이 공유하는 게임 번호
	
    private final int id;
    private ClientHandler host;
    private ClientHandler guest;
    private boolean guestReady = false;
    private Theme theme = Theme.BASIC; // 방장이 안 고르면 기본 테마
    private RoomState state = RoomState.WAITING;

    
    public GameRoom(int id) {
        this.id = id;
    }

    
    // 들어갈 수 있으면 자리 배정하고 true, 꽉 찼거나 게임 중이면 false
    public synchronized boolean tryJoin(ClientHandler player) {
        if (state != RoomState.WAITING || guest != null) {
            return false;
        }
        
        if (host == null) {
            host = player;
            player.send(new JoinedMessage(id, Role.HOST));
        }
        
        else {
            guest = player;
            player.send(new JoinedMessage(id, Role.GUEST));
            host.send(new OpponentMessage(player.getNickname()));    // 방장에게 상대 정보
            player.send(new OpponentMessage(host.getNickname()));    // 참가자에게 상대 정보
        }
        
        return true;
    }
    
    
    // 채팅
    public synchronized void chat(ClientHandler sender, String content) {
        broadcast(new ChatMessage(sender.getNickname(), content)); // 보낸 사람 닉네임은 서버가 붙임
    }
    
    
    // 준비 (참가자만)
    public synchronized void setReady(ClientHandler sender, boolean ready) {
        if (sender != guest) {
            return; // 참가자가 아니면 조용히 무시
        }
        guestReady = ready;
        broadcast(new ReadyMessage(ready)); // 방장 + 참가자 둘 다
    }
    
    
    //  테마 (방장만)
    public synchronized void setTheme(ClientHandler sender, Theme newTheme) {
        if (sender != host) {
            sender.send(new ErrorMessage(ErrorCode.NOT_HOST));
            return;
        }
        theme = newTheme; // 게임 시작 때 쓰려고 저장
        broadcast(new ThemeMessage(theme)); // 테마 변경을 전체 공지
    }
    
    
    // 시작 (방장만)
    public synchronized void start(ClientHandler sender) {
        if (sender != host) {
            sender.send(new ErrorMessage(ErrorCode.NOT_HOST));
            return;
        }
        
        if (guest == null || !guestReady || state == RoomState.PLAYING) {
            return; // 시작 조건이 아니면 조용히 무시
        }
        
        state = RoomState.PLAYING;

        int gameId = NEXT_GAME_ID.getAndIncrement();
        PlayerProfile p1 = new PlayerProfile(1, host.getNickname());
        PlayerProfile p2 = new PlayerProfile(2, guest.getNickname());

        // myPlayerId가 사람마다 달라서 broadcast 대신 따로 전송
        host.send(new GameStartMessage(gameId, theme, p1, p2, 1));
        guest.send(new GameStartMessage(gameId, theme, p1, p2, 2));

        // TODO (게임 연동): 게임 로직 시작 (theme, p1, p2 전달)
    }
    
    
    // 퇴장
    public synchronized void leave(ClientHandler leaver) {
        boolean hostLeft;
        
        if (leaver == guest) {
            guest = null;
            hostLeft = false;
        }
        
        else if (leaver == host) {
            host = guest; // 남은 참가자가 방장 승계
            guest = null;
            hostLeft = true;
        }
        
        else {
            return;
        }

        guestReady = false;
        state = RoomState.WAITING;                  // TODO (게임 연동): 게임 중이었다면 게임 로직 정리

        if (host != null) { // 둘 다 나간 게 아닐 때
            host.send(SignalMessage.opponentLeft());
            if (hostLeft) { // 승계됐다고 알려주기
                host.send(new RoleMessage(Role.HOST));
            }
        }
    }
    
    
    // ===== 공통 =====
    private void broadcast(Message message) {
        if (host != null) host.send(message);
        if (guest != null) guest.send(message);
    }

    
    public synchronized boolean isEmpty() {
        return host == null && guest == null;
    }

    
    public int getId() {
        return id;
    }
    
}
