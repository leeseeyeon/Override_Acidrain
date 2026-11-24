package server.game.item;

import java.util.ArrayList;
import java.util.List;

import server.game.player.Player;
import server.game.session.GameSession;

public class ItemManager {

    private List<Item> activeItems;
    private ItemFactory itemFactory;
    private double spawnProbability;
    private GameSession session;

    // 생성자
    public ItemManager(GameSession session, double spawnProbability) {
        this.activeItems = new ArrayList<>();
        this.itemFactory = new ItemFactory();
        this.spawnProbability = spawnProbability;
        this.session = session;
    }

    // 확률에 따라 랜덤 아이템 생성
    public synchronized Item spawnRandomItem(String word, float speed) {

        if (Math.random() >= spawnProbability) {
            return null;
        }

        Item item = itemFactory.createRandomItem(word, speed);
        activeItems.add(item);

        return item;
    }

    // 아이템 단어 입력 처리
    public synchronized boolean processItemInput(int playerId, int itemId, String text) {

        // 입력한 플레이어와 상대방 가져오기
        Player user = session.getPlayer(playerId);
        Player opponent = session.getOpponent(playerId);

        // 유효하지 않은 플레이어인 경우
        if (user == null || opponent == null) {
            return false;
        }

        for (Item item : activeItems) {

            // 아이템 ID와 입력 단어가 일치하는지 확인
            if (item.getItemId() == itemId
                    && item.getWord().equals(text)) {

                // 아이템 획득 가능 여부 확인
                if (item.claim(playerId)) {

                    // 아이템 효과 적용
                    item.applyEffect(user, opponent);

                    // 획득한 아이템 제거
                    removeItem(itemId);

                    return true;
                }

                return false;
            }
        }

        return false;
    }

    // 특정 아이템 삭제
    public synchronized void removeItem(int itemId) {

        activeItems.removeIf(
                item -> item.getItemId() == itemId);
    }

    // 유효시간이 지난 아이템 삭제
    public synchronized void removeExpiredItems(long now) {

        activeItems.removeIf(
                item -> item.isExpired(now));
    }

    // 현재 활성화된 아이템 목록 반환
    public synchronized List<Item> getActiveItems() {

        return new ArrayList<>(activeItems);
    }
}