package client.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

// 서버 메시지를 계속 받는 스레드 (받으면 ServerConnection에 넘기고, 서버 쪽에서 끊기면 알림)
public class ClientReceiveThread implements Runnable {
	private final Socket socket;
	private final ServerConnection conn;
	
	public ClientReceiveThread(Socket socket, ServerConnection conn) {
		this.socket = socket;
		this.conn = conn;
	}

	@Override
	public void run() {
	    String error = null;
	    try {
	        receiveLoop();
	    } catch (IOException e) {
	        error = e.getMessage();
	    }
	    handleServerClosed(error);
	}

	// 서버 메시지를 한 줄씩 받아 ServerConnection에 넘김 (연결이 끊기면 종료)
	private void receiveLoop() throws IOException {
	    BufferedReader in = new BufferedReader(
	            new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
	    String line;
	    while ((line = in.readLine()) != null) {
	        conn.notifyListeners(line);
	    }
	}

	// 서버 쪽에서 끊긴 경우에만 알림 (내가 disconnect()로 닫은 경우는 제외)
	private void handleServerClosed(String error) {
	    if (socket.isClosed()) return;
	    if (error != null) {
	        System.out.println("서버로부터 메시지 수신 중 오류 : " + error);
	    }
	    System.out.println("[알람] 서버와의 연결이 끊어졌습니다.");
	    conn.notifyDisconnected();
	}
}
