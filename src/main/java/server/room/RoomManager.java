package server.room;

import java.util.ArrayList;
import java.util.List;

import server.network.ClientHandler;

public class RoomManager {
    private static final RoomManager INSTANCE = new RoomManager();   // 클래스 로딩 때 딱 하나 생성

    private final List<GameRoom> rooms = new ArrayList<>();
    private int nextId = 1;

    private RoomManager() {}                    // 밖에서 new 금지

    public static RoomManager getInstance() {
        return INSTANCE;
    }

    // 빈자리 있는 방에 넣고, 없으면 새 방을 만들어서 넣음
    public synchronized GameRoom assignRoom(ClientHandler player) {
        for (GameRoom room : rooms) {
            if (room.tryJoin(player)) {
                return room;
            }
        }
        GameRoom room = new GameRoom(nextId++);
        room.tryJoin(player);                    // 새 방이니까 방장으로 들어감
        rooms.add(room);
        return room;
    }

    public synchronized void removeRoom(GameRoom room) {
        if (room.isEmpty()) {
            rooms.remove(room);
            System.out.println("[서버] 방 " + room.getId() + " 삭제");
        }
    }
}
