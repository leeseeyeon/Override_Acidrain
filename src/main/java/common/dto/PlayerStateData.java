package common.dto;

public class PlayerStateData {
	private int playerId;
	private String nickname;
	private int currentHp;
	private double cpm;
	private double accuracy;

	//PlayerState 기본 생성자
	private PlayerStateData() {}
	
	public PlayerStateData(int Id, String name, int currentHp, double cpm, double accuracy) {
		this.playerId = Id;
		this.nickname = name;
		this.currentHp = currentHp;
		this.cpm = cpm;
		this.accuracy = accuracy;
	}
	
	//데이터를 한번에 업데이트 하는 메소드 
	public PlayerStateData(int Id, int hp, double cpm, double accuracy) {
		this.playerId = Id;
		this.currentHp = hp;
		this.cpm = cpm;
		this.accuracy =accuracy;
	}
	
	//getter 메소드
	public int getplayerId() {
		return this.playerId;
	}
	public int getCurrentHp() {
		return this.currentHp;
	}
	public String getNickname() {
		return this.nickname;
	}
	public double getCpm() {
		return this.cpm;
	}
	public double Accuracy() {
		return this.accuracy;
	}
}

