package common.message.lobby;

import common.message.Message;
import common.message.MessageType;

public class JoinMessage extends Message {
	private String nickname;
	
	// decode할 때 Gson이 쓰는 생성자, gson은 private 생성자도 사용할 수 있음
	private JoinMessage() {
		super(MessageType.JOIN);
	}
	
	// 클라이언트가 서버로 보낼 때 쓰는 생성자
	public JoinMessage(String nickname) {
		this();
		this.nickname = nickname;
	}
	
	public String getNickname() {
		return nickname;
	}
}
