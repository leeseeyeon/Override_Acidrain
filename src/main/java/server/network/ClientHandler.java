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
import common.message.game.WordSubmitMessage;
import server.room.GameRoom;
import server.room.RoomManager;

// 클라이언트 한 명과의 소켓 연결 담당: 메시지를 받아 방(GameRoom)에 전달하고, 서버 메시지를 클라로 보냄
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
        try (BufferedReader br = openStreams()) {
            receiveLoop(br);
            log("연결 종료");
        } catch (IOException e) {
            log("연결 오류: " + e.getMessage());
        } finally {
            leaveRoom();
            closeSocket();
        }
    }

    // 송수신 준비: 보내기용 pw 만들고, 받기용 reader 반환
    private BufferedReader openStreams() throws IOException {
        socket.setTcpNoDelay(true);   // 작은 메시지 모아 보내지 않고 바로 전송 (게임 상태 지연 줄이기)
        pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        return new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    }

    // 연결이 끊길 때까지 한 줄씩 받아서 처리
    private void receiveLoop(BufferedReader br) throws IOException {
        String request;
        while ((request = br.readLine()) != null) {   // 메시지 올 때까지 대기
            handle(request);
        }
    }

    // 소켓 닫기 (이미 닫혀 있으면 넘어감)
    private void closeSocket() {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 받은 메시지를 type별로 나눠 방(GameRoom)에 전달 (입장 전에는 JOIN만 처리)
    private void handle(String request) {
        Message m = JsonUtil.decodeOrNull(request, "ClientHandler");
        if (m == null) {
            return;
        }
        
        if (m.getType() != MessageType.JOIN && room == null) {
            log("입장 전 메시지 무시: " + m.getType());
            return;
        }

        switch (m.getType()) {
            case MessageType.JOIN -> handleJoin((JoinMessage) m);
            case MessageType.CHAT  -> room.chat(this, ((ChatMessage) m).getContent());
            case MessageType.READY -> room.setReady(this, ((ReadyMessage) m).isReady());
            case MessageType.THEME -> room.setTheme(this, ((ThemeMessage) m).getTheme());
            case MessageType.START -> room.start(this);
            case MessageType.WORD_SUBMIT -> {
                WordSubmitMessage w = (WordSubmitMessage) m;
                room.submitWord(this, w.getWord(), w.getTypingTime());
            }
            case MessageType.GAME_QUIT -> leaveRoom();
            default -> log("아직 처리하지 않는 메시지: " + m.getType());
        }
    }

    // 닉네임을 저장하고 RoomManager에서 방을 배정받음
    private void handleJoin(JoinMessage m) {
        if (room != null) return; // 이미 방에 있으면 무시
        nickname = m.getNickname();
        room = RoomManager.getInstance().assignRoom(this);
        log("방 " + room.getId() + " 입장");
    }

    // 방 나가기 (GAME_QUIT, 연결 끊김 공통)
    private void leaveRoom() {
        if (room == null) return;
        room.leave(this);
        RoomManager.getInstance().removeRoom(room);   // 비었을 때만 삭제됨
        room = null;
    }

    // 이 클라이언트에게 메시지 전송 (여러 스레드에서 동시에 보내도 안 섞이게 synchronized)
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