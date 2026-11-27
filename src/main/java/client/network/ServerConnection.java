package client.network;

import common.message.Message;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import common.message.JsonUtil;

// 클라이언트의 서버 연결 담당: 접속, 메시지 전송, 받은 메시지를 등록된 화면(리스너)들에게 전달
public class ServerConnection {
	private Socket socket;
	private PrintWriter out;
	private final List<MessageListener> listeners = new CopyOnWriteArrayList<>();
	
	// 서버에 접속하고 수신 스레드 시작
	public void connect(String host, int port) throws IOException {
		socket = new Socket(host, port);
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
		new Thread(new ClientReceiveThread(socket, this)).start();
	}
	
	// 메시지를 JSON으로 바꿔 서버로 전송
	public void send(Message message) {
		out.println(JsonUtil.encode(message)); // 팀원은 JSON 몰라도 됨
	}
	
	// 서버 메시지를 받을 화면 등록
	public void addListener(MessageListener listener) {
		listeners.add(listener);
	}
	
	// 화면 전환 시 더 이상 메시지를 안 받도록 등록 해제
	public void removeListener(MessageListener listener) {
		listeners.remove(listener);
	}
	
	// ClientReceiveThread만 호출하므로 default
	void notifyListeners(String msg) {
		for (MessageListener ml : listeners) {
			ml.update(msg);
		}
	}
	
	// '메인으로' 버튼이나 창 닫는 용도
	public void disconnect() {
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			System.out.println("연결 종료 중 오류: " + e.getMessage()); // 오류메시지를 간단히
		}
		
	}
	
	// 서버 쪽에서 연결이 끊겼을 때: 소켓 정리 후 모든 화면에 알림
	void notifyDisconnected() {
	    disconnect();  // 소켓 정리
	    for (MessageListener ml : listeners) {
	    	ml.onDisconnected();
	    }
	}

}
