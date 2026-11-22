package server.game.item;

import server.game.player.Player;

public class BombEffect implements ItemEffect{
	
	@Override
	public void apply(Player user, Player opponent) {
		opponent.decreaseHP(10);
	}

}
