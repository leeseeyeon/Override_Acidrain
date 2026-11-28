package common.message.game;

import common.message.Message;
import common.message.MessageType;

public class WordSubmitMessage extends Message {
	private String word;
	private double typingTime;
	
	private WordSubmitMessage() {
		super(MessageType.WORD_SUBMIT);
	}
	
	public WordSubmitMessage(String word, double typingTime) {
		this();
		this.word = word;
		this.typingTime = typingTime;
	}
	
	public String getWord() {
		return word;
	}
	
	public double getTypingTime() {
		return typingTime;
	}

}
