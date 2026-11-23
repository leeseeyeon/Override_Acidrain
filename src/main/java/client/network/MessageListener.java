package client.network;

public interface MessageListener {
	
	public void update(String msg);
	
	default void onDisconnected() {}; // 서버 쪽에서 연결이 끊겼을 때만 호출됨

}
