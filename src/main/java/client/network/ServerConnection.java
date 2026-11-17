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

public class ServerConnection {
	private Socket socket;
	private PrintWriter out;
	private final List<MessageListener> listeners = new CopyOnWriteArrayList<>();
	
	public void connect(String host, int port) throws IOException {
		socket = new Socket(host, port);
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
		new Thread(new ChatClientReceiveThread(socket, this)).start();
	}
	
	public void send(Message message) {
		out.println(JsonUtil.encode(message)); // 팀원은 JSON 몰라도 됨
	}
	
	public void addListener(MessageListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(MessageListener listener) {
		listeners.remove(listener);
	}
	
	// ChatClientReceiveThread만 호출하므로 default
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

}
