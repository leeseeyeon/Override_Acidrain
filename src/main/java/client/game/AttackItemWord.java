package client.game;
import java.awt.Color;

public class AttackItemWord extends ClientFallingWord{

	private String effectType;
	private Color highlightColor;
	
	//공격 아이템 생성자
	public AttackItemWord(int id, String text, String effect) {
		super(id, text);
		this.effectType = effect;
	}
	
	public String getEffectType() {
		return effectType;
	}
}
