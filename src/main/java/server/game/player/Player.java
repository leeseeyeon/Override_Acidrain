package server.game.player;

public class Player {
	private int playerId;
	private String nickname;
	private TypingStats typingStats;
	private int hp;
	private static final int MAX_HP = 100;
	
	public Player(int playerid, String nickname) {
		this.playerId = playerid;
		this.nickname = nickname;
		this.hp = MAX_HP;
		this.typingStats = new TypingStats();
	}
	
	public void decreaseHP(int amount) {
		this.hp = hp-amount;
		if (hp <= 0) {
			this.hp = 0;
		}
	}
	
	public void increaseHP(int amount) {
		this.hp = hp+amount;
		if (hp>=MAX_HP) {
			this.hp = MAX_HP;
		}
	}
	
	public boolean isDead() {
		return hp <=0;
	}
	
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
