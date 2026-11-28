package common.message.lobby;

import common.message.Message;
import common.message.MessageType;

public class ReadyMessage extends Message {
	private boolean ready;
	
	// decode할 때 Gson이 쓰는 생성자, gson은 private 생성자도 사용할 수 있음
	private ReadyMessage() {
		super(MessageType.READY);
	}
	
	// 참가자가 서버로 준비 상태를 보낼 때 쓰는 생성자
	public ReadyMessage(boolean ready) {
		this();
		this.ready = ready;
	}
	
	public boolean isReady() {
		return ready;
	}

}
