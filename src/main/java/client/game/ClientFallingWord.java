package client.game;

import javax.swing.Timer;
import java.util.Random;

abstract class ClientFallingWord {
	private int wordId;
	private	long lastTime=0;
	protected String text;
	protected float x;
	protected float y;
	
	Random random = new Random();
	
	//단어 생성자
	public ClientFallingWord(int id, String text) {
		this.wordId = id;
		this.text = text;
		//x값 범위는 후에 결정 예정
		x = random.nextFloat(4)+1;
	}
	
	public void moveWord(float fallingSpeed) {
		// TODO : 게임 루프 속도에 맞춰 y 좌표를 감소시키는 로직 구현
		long now;
		float deltatime;
		now = System.currentTimeMillis();
		deltatime = (now - lastTime)/1000.0f;
		//20은 추후 변경 예정인 값
		y = y - 20 * fallingSpeed * deltatime; 
		lastTime = now;
	}
}
