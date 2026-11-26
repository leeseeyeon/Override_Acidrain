package server.game.item;

import common.type.ItemType;
import server.game.player.*;
public class Item {
	private int itemId; //아이템 고유번호
	private String word; // 아이템을 획득하기 위해 입력해야하는 단어
	private ItemEffect effect; //아이템 초과
	private boolean isActive; //현재 획득가능한지(활성화된 단어)
	private Integer claimedBy; //어떤 플레이어가 먼저 획득했는지 , Integer은 null을 쓸 수 있다.
	private long spawnTime; //아이템이 생성된 시각
	private long reachBottomTime; //아이템이 바닥에 도달하는 시간
	private ItemType type;
	private float y; //y좌표
	
	
	
	public Item(int itemId, String word, ItemEffect effect, long spawnTime, long reachBottomTime, ItemType type) {
		this.itemId = itemId;
		this.word = word;
		this.effect = effect;
		this.spawnTime = spawnTime;
		this.reachBottomTime = reachBottomTime;
		
		this.isActive = true;
		this.claimedBy = null;
		
		this.type = type;
		this.y =0;
	}
	
	//플레이어가 아이템을 획득하도록 처리 synchronized : 동기화 처리 같은 item 객체의 claim은 한 번에 하나의 스레드에만 실행할 수 있다.
	public synchronized boolean claim (int playerId) {
		//이미 획득되었거나 비활성화된 아이템이면 실패
		if((!isActive||claimedBy != null)) {
			return false;
		} 
		
		//유효시간이 지난 아이템이면 획득 불가능
		if(isExpired(System.currentTimeMillis())) {
			return false;
		}
		
		//먼저 입력한 플레이어가 아이템 획득
		claimedBy = playerId;
		isActive = false;
		
		return true;
	}
	
	
	//아이템이 유효시간을 지났는지 확인
	public boolean isExpired(long now) {
		return now>=reachBottomTime;
	}
	
	public void updatePosition(long now, float bottomY) {
		long totalTime = reachBottomTime - spawnTime;
		long elapsedTime = now - spawnTime;
		
		if (totalTime <=0) {
			y=bottomY;
			return;
		}
		
		float progress = (float) elapsedTime / totalTime;
		
		progress = Math.max(0, Math.min(1, progress));
		y=bottomY*progress;
	}
	
	//획득한 아이템의 효과 적용
	public void applyEffect(Player user, Player opponent) {
		if (effect !=null) {
			effect.apply(user,opponent);
		}
	}
	
	//Getter
	public int getItemId() {
		return itemId;
	}
	
	public String getWord() {
		return word;
	}
	
	public ItemEffect getEffect() {
		return effect;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public Integer getClaimedBy() {
		return claimedBy;
	}
	
	public long getSpawnTime() {
		return spawnTime;
	}
	
	public long getReachBottomTime() {
		return reachBottomTime;
	}
	
	public ItemType getType() {
		return type;
	}
	
	public float getY() {
		return y;
	}
}
