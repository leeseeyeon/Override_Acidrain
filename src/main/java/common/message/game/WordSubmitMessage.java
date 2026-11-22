package common.message.game;

import common.message.Message;
import common.message.MessageType;

public class WordSubmitMessage extends Message {
	private int wordId;
	private String word;
	private long inputTime;
	
	private WordSubmitMessage() {
		super(MessageType.WORD_SUBMIT);
	}
	
	public WordSubmitMessage(int wordId, String word, long inputTime) {
		this();
		this.wordId = wordId;
		this.word = word;
		this.inputTime = inputTime;
	}
	
	public int getWordId() {
		return wordId;
	}
	
	public String getWord() {
		return word;
	}
	
	public long getInputTime() {
		return inputTime;
	}

}
