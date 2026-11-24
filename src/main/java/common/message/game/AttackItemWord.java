package common.message.game;

public class AttackItemWord extends ClientFallingWord{

	private String effectType;
	
	//공격 아이템 생성자
	public AttackItemWord(int id, String text, String effect, float x, float y) {
		super(id, text,x,y);
		this.effectType = effect;
	}
	
	public String getEffectType() {
		return effectType;
	}
}
