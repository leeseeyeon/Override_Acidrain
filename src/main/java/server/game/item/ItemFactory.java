package server.game.item;

import java.util.Random;
import common.ItemType;
public class ItemFactory {
	private Random random = new Random();
	private int nextItemId = 1;
	
	public Item createRandomItem(String word, float speed) {
		ItemEffect effect = null;
		ItemType type = null;
		
		//세 가지 아이템 중 랜덤 선택
		int randomType = random.nextInt(3);
		
		switch(randomType) {
		case 0:
			effect = new BombEffect();
			type = ItemType.BOMB;
			break;
		case 1:
			effect = new ShadowEffect();
			type = ItemType.SHADOW;
			break;
		case 2: 
			effect = new HealEffect();
			type = ItemType.HEAL;
			break;
		}
		
		long spawnTime = System.currentTimeMillis();
		
		//임시 제한 시간 (speed가 클수록 짧아짐)
		long duration = Math.round(10000f/speed);
		long reachBottomTime = spawnTime + duration;
		
		return new Item(nextItemId++, word, effect,spawnTime, reachBottomTime, type);
				
	}
}
