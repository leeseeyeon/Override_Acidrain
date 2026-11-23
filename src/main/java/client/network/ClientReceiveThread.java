package client.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
	        BufferedReader in = new BufferedReader(
	                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
	        String line;
	        while ((line = in.readLine()) != null) {
	            conn.notifyListeners(line);
	        }
			
		} catch (IOException e) {
			error = e.getMessage();
			System.out.println("서버로부터 메시지 수신 중 오류 : " + e.getMessage());
		}
		
		if (!socket.isClosed()) { // 서버 쪽에서 끊김
			if (error != null) {
	            System.out.println("서버로부터 메시지 수신 중 오류 : " + error);
	        }
			System.out.println("[알람] 서버와의 연결이 끊어졌습니다.");
			conn.notifyDisconnected();
		}
	}
}
