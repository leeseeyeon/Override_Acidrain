package common.message.game;

import common.message.Message;
import common.message.MessageType;

public class ItemInputMessage extends Message {
    private int itemId;
    private String text;
    private long inputTime;

    private ItemInputMessage() {
        super(MessageType.ITEM_INPUT);
    }

    // 클라이언트가 보낼 때
    public ItemInputMessage(int itemId, String text, long inputTime) {
        this();
        this.itemId = itemId;
        this.text = text;
        this.inputTime = inputTime;
    }

    public int getItemId() {
    	return itemId;
    }
    
    public String getText() {
    	return text;
    }
    
    public long getInputTime() {
    	return inputTime;
    }
}