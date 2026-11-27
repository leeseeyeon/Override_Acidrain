package server.game.player;
import common.config.GameConfig;
public class Player {
	private int playerId; //playerid _ 대기실에서 할당됨
	private String nickname; //설정된 nickname
	private TypingStats typingStats; //타수
	private int hp; 
	private static final int MAX_HP = GameConfig.MAX_HP; //최대 100
	
	//player 생성자
	public Player(int playerid, String nickname) {
		this.playerId = playerid;
		this.nickname = nickname;
		this.hp = MAX_HP;
		this.typingStats = new TypingStats();
	}
	
	//피 감소
	public void decreaseHP(int amount) {
		this.hp = hp-amount;
		if (hp <= 0) {
			this.hp = 0;
		}
	}
	 
	//피 증가
	public void increaseHP(int amount) {
		this.hp = hp+amount;
		if (hp>=MAX_HP) {
			this.hp = MAX_HP;
		}
	}
	
	//즉었는지 체크-> win판정시 사용
	public boolean isDead() {
		return hp <=0;
	}
	
	//getter
	public int getPlayerId() {
		return playerId;		
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public int getHP() {
		return hp;
	}
	
	public TypingStats getTypingStats() {
		return typingStats;
	}
	
	
}
