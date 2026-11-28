package client.network;

// 서버 메시지를 받을 화면이 구현하는 인터페이스 (메시지 수신 update, 연결 끊김 onDisconnected)
public interface MessageListener {
	
	public void update(String msg); // 서버 메시지 한 줄(JSON)을 받았을 때 호출
	
	default void onDisconnected() {} // 서버 쪽에서 연결이 끊겼을 때만 호출됨

}
