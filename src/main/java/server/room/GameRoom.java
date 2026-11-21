package server.room;

import common.message.Role;
import common.message.lobby.JoinedMessage;
import common.message.lobby.OpponentMessage;
import server.network.ClientHandler;

public class GameRoom {
    private final int id;
    private ClientHandler host;
    private ClientHandler guest;
    private boolean guestReady = false;
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

    public synchronized boolean isEmpty() {
        return host == null && guest == null;
    }

    public int getId() {
        return id;
    }
}
