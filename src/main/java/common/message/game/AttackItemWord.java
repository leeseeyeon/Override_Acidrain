package common.message.game;

public class AttackItemWord {
	private String itemtype;
    private String text;
    private float y;

    private AttackItemWord() {}

    public AttackItemWord(String itemtype, String text, float y) {
        this.itemtype = itemtype;
        this.text = text;
        this.y = y;
    }

    public String getItemtype() {
    	return itemtype;
    }
    
    public String getText() {
    	return text;
    }
    
    public float getY() {
    	return y;
    }
}
