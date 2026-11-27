package common.message;

public abstract class Message {
	protected MessageType type;
	
	protected Message(MessageType type) {
		this.type = type;
	}
	
	public MessageType getType() {
		return type;
	}

}
