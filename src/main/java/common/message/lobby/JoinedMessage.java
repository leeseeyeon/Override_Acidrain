package common.message.lobby;

import common.message.Message;
import common.message.MessageType;
import common.message.Role;

public class JoinedMessage extends Message {
	private int roomId;
	private Role role;
	
	// decode할 때 Gson이 쓰는 생성자, gson은 private 생성자도 사용할 수 있음
	private JoinedMessage() {
		super(MessageType.JOINED);
	}
	
	// 서버가 입장 결과를 보낼 때
	public JoinedMessage(int roomId, Role role) {
		this();
		this.roomId = roomId;
		this.role = role;
	}
	
	public int getRoomId() {
		return roomId;
	}
	
	public Role getRole() {
		return role;
	}

}
