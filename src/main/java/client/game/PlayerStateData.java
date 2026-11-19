package client.game;

public class PlayerStateData {
	private int playerId;
	private String nickname;
	private int currentHp;
	private int maxHp = 100;
	private double cpm;
	private double accuracy;


	//생성자
	//PlayerState 기본 생성자
	public PlayerStateData() {}
	
	//초기값을 업데이트하는 메소드
	public PlayerStateData(int Id, String name) {
		this.playerId = Id;
		this.nickname = name;
	}
	//데이터를 한번에 업데이트 하는 메소드 
	public PlayerStateData(int hp, double cpm, double accuracy) {
		this.currentHp = hp;
		this.cpm = cpm;
		this.accuracy =accuracy;
	}
	
	//getter 메소드
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


