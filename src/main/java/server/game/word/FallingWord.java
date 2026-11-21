package server.game.word;

public class FallingWord {
	private int WordId;
	private String text;
	private float x;
	private float y;
	private long spawnTime;
	private long reachBottomTime;
	private boolean completed = false;
	
	public FallingWord(int wordId, String text, float x, float y, long spawnTime, long reachBottomTime) {
		this.WordId = wordId;
		this.text = text;
		this.x = x;
		this.y = y;
		this.spawnTime = spawnTime;
		this.reachBottomTime = reachBottomTime;
		this.completed = false;
	}
	
	//단어 입력완료처리
	public void markCompleted() {
		this.completed = true;
	}
	
	//입력완료여부 확인
	public boolean isCompleted() {
		return completed;
	}
	
	//바닥 도달여부 확인
	public boolean hasReachedBottom(long now) {
		return now >=reachBottomTime;
	}
	
	//단어 ID반환
	public int getWordId() {
		return WordId;
	}
	
	//단어 문자열 반환
	public String getText() {
		return text;
	}
	
	//x좌표 반환
	public float getX() {
		return x;
	}
	
	//y좌표 반환
	public float getY() {
		return y;
	}
	
	//단어생성시각 반환
	public long getSpawnTime() {
		return spawnTime;
	}
	
	//바닥 도착 예정 시각 반환
	public long getReachBottomTime() {
		return reachBottomTime;
	}
	
	//서버에서 계산한 y좌표 저장
	public void setY(float y) {
		this.y = y;
	}
	
}
