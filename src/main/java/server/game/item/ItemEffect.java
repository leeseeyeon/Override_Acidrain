package server.game.item;

import server.game.player.*;
public interface ItemEffect {
	public void apply(Player user, Player opponent);
}
