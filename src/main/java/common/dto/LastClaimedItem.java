package common.dto;

import common.type.ItemType;

// 가장 최근에 누가 어떤 아이템을 먹었는지 (클라는 itemId로 중복 처리 방지)
public class LastClaimedItem {
	private int itemId;
	private ItemType type;      // BOMB, HEAL, SHADOW
	private int claimedBy;    // 먹은 플레이어 playerId

	public LastClaimedItem(int itemId, ItemType type, int claimedBy) {
		this.itemId = itemId;
		this.type = type;
		this.claimedBy = claimedBy;
	}

	public int getItemId() {
		return itemId;
	}

	public ItemType getType() {
		return type;
	}

	public int getClaimedBy() {
		return claimedBy;
	}
}