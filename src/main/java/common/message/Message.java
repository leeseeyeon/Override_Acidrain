package common.message;

public abstract class Message {
	protected String type;
	
	protected Message(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

}
