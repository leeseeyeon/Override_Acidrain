package common.message.game;

public class ClientFallingWord {
	private int wordId;
    private String text;
    private float x;
    private float y;

    private ClientFallingWord() {}

    public ClientFallingWord(int wordId, String text, float x, float y) {
        this.wordId = wordId;
        this.text = text;
        this.x = x;
        this.y = y;
    }

    public int getWordId() {
    	return wordId;
    }
    
    public String getText() {
    	return text;
    }
    
    public float getX() {
    	return x;
    }
    
    public float getY() {
    	return y;
    }
}
