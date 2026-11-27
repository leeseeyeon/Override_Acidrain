package common.message;

// 모든 메시지의 부모 클래스 (type 필드로 어떤 메시지인지 구분)
public abstract class Message {
	protected MessageType type;
	
	protected Message(MessageType type) {
		this.type = type;
	}
	
	public MessageType getType() {
		return type;
	}

}
