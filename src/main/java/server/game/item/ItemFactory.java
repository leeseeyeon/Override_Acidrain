package server.game.item;

import java.util.Random;

public class ItemFactory {
	private Random random = new Random();
	private int nextItemId = 1;
	
	public Item createRandomItem(String word, float speed) {
		ItemEffect effect = null;
		
		//세 가지 아이템 중 랜덤 선택
		int randomType = random.nextInt(3);
		
		switch(randomType) {
		case 0:
			effect = new BombEffect();
			break;
		case 1:
			effect = new ShadowEffect();
			break;
		case 2: 
			effect = new HealEffect();
			break;
		}
		
		long spawnTime = System.currentTimeMillis();
		
		//임시 제한 시간 (speed가 클수록 짧아짐)
		long duration = (long)(10000/speed);
		long reachBottomTime = spawnTime + duration;
		
		return new Item(nextItemId++, word, effect,spawnTime, reachBottomTime);
				
	}
}
