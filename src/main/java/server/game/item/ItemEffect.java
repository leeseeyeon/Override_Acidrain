package server.game.item;

import server.game.player.Player;
public interface ItemEffect {
	public void apply(Player user, Player opponent);
}
