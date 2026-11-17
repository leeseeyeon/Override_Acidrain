package client.game;

public class ClientFallingWord {
	private int wordId;
	protected String text;
	protected float x;
	protected float y;
	protected FallingWordType type;
	
	public ClientFallingWord(int id, String text, float x, float y) {
		
	}
	public void moveWord(float fallingSpeed) {
		// TODO : 게임 루프 속도에 맞춰 y 좌표를 감소시키는 로직 구현
	}
}
