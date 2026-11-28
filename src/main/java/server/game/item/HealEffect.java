package server.game.item;

import server.game.player.Player;

public class HealEffect implements ItemEffect {
	@Override
	public void apply(Player user, Player opponent) {
		user.increaseHP(10); //내 hp 10 상승. 최대 100으로 막혀있음.
	}

}
