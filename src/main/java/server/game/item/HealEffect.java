package server.game.item;

import server.game.player.Player;

public class HealEffect implements ItemEffect {
	@Override
	public void apply(Player user, Player opponent) {
		user.increaseHP(10);
	}

}
