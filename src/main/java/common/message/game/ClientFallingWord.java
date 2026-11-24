package common.message.game;

import java.util.Random;

public abstract class ClientFallingWord {
	public int wordId;
	public String text;
	public float x;
	public float y;
	
	Random random = new Random();
	
	//단어 생성자
	public ClientFallingWord(int id, String text,float x, float y) {
		this.wordId = id;
		this.text = text;
		this.x=x;
		this.y=y;
	}
	

	public String getText() {
		return text;
	}
	public float getY() {
		return y;
	}
	public float getX() {
		return x;
	}
}
