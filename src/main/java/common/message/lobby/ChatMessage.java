package common.message.lobby;

import common.message.Message;
import common.message.MessageType;

public class ChatMessage extends Message {
	private String nickname;
	private String content;
	
	// decode할 때 Gson이 쓰는 생성자, gson은 private 생성자도 사용할 수 있음
	private ChatMessage() {
		super(MessageType.CHAT);
	}
	
	// 클라이언트가 서버로 보낼 때 쓰는 생성자
	public ChatMessage(String content) {
		this();
		this.content = content;
	}
	
	// 서버가 채팅을 방 전체에 전달하는 생성자
	public ChatMessage(String nickname, String content) {
		this(content);
		this.nickname = nickname;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public String getContent() {
		return content;
	}

}
