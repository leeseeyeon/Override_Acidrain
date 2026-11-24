package server.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import common.message.JsonUtil;
import common.message.Message;
import common.message.MessageType;
import common.message.lobby.*;
import server.room.GameRoom;
import server.room.RoomManager;

public class ClientHandler implements Runnable {
    private String nickname;
    private final Socket socket;
    private GameRoom room;
    private PrintWriter pw;
    private int playerId;

	public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
            pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            String request;
            while ((request = br.readLine()) != null) {   // 메시지 올 때까지 대기
                handle(request);
            }
            log("연결 종료");
        } catch (IOException e) {
            log("연결 오류: " + e.getMessage());
        } 
        
        finally {
        	if (room != null) {
                room.leave(this);
                RoomManager.getInstance().removeRoom(room);   // 비었을 때만 삭제됨
            }
        	
            try {
                if (!socket.isClosed()) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handle(String request) {
        Message m;
        try {
            m = JsonUtil.decode(request);
        } catch (Exception e) {
            log("해석할 수 없는 메시지: " + request);
            return;
        }
        
        if (!m.getType().equals(MessageType.JOIN) && room == null) {
            log("입장 전 메시지 무시: " + m.getType());
            return;
        }

        switch (m.getType()) {
            case MessageType.JOIN -> handleJoin((JoinMessage) m);
            case MessageType.CHAT  -> room.chat(this, ((ChatMessage) m).getContent());
            case MessageType.READY -> room.setReady(this, ((ReadyMessage) m).isReady());
            case MessageType.THEME -> room.setTheme(this, ((ThemeMessage) m).getTheme());
            case MessageType.START -> room.start(this);
            // TODO (게임 연동): WORD_SUBMIT, ITEM_INPUT, GAME_QUIT
            default -> log("아직 처리하지 않는 메시지: " + m.getType());
        }
    }

    private void handleJoin(JoinMessage m) {
        if (room != null) return; // 이미 방에 있으면 무시
        nickname = m.getNickname();
        room = RoomManager.getInstance().assignRoom(this);
        log("방 " + room.getId() + " 입장");
    }

    public synchronized void send(Message message) {
        pw.println(JsonUtil.encode(message));
    }

    public String getNickname() {
        return nickname;
    }
    
    public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

    private void log(String msg) {
        System.out.println("[" + (nickname != null ? nickname : socket.getRemoteSocketAddress()) + "] " + msg);
    }
}