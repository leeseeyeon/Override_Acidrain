package server.room;

import java.util.ArrayList;
import java.util.List;

import common.message.game.AttackItemWord;
import common.message.game.ClientFallingWord;
import common.message.game.GameStateUpdateMessage;
import common.message.game.PlayerStateData;
import server.game.ServerGameManager;
import server.game.item.Item;
import server.game.player.Player;
import server.game.player.TypingStats;
import server.game.word.FallingWord;

// 게임 로직 상태 → GAME_STATE_UPDATE 메시지로 변환 (게임 로직은 메시지 형식을 몰라도 되게)
final class GameStateMapper {

    private GameStateMapper() {}

    // 두 클라이언트가 같은 화면을 보므로 양쪽 판 정보를 다 담음
    static GameStateUpdateMessage toMessage(ServerGameManager game) {
        Player p1 = game.getSession().getPlayer1();
        Player p2 = game.getSession().getPlayer2();

        return new GameStateUpdateMessage(
                List.of(toPlayerState(p1), toPlayerState(p2)),
                toClientWords(game, p1),
                toClientWords(game, p2),
                toAttackItem(game)); // TODO (채원님 리뷰 반영 후): 아이템 정보 생기면 AttackItemWord 채우기
    }

    private static PlayerStateData toPlayerState(Player p) {
        TypingStats stats = p.getTypingStats();
        return new PlayerStateData(p.getPlayerId(), p.getNickname(), p.getHP(),
                stats.calculateCPM(), stats.calculateAccuracy());
    }
    
    // 화면 가운데 공통 제시어: 아직 아무도 못 먹은 아이템 하나 (없으면 null)
    private static AttackItemWord toAttackItem(ServerGameManager game) {
        for (Item item : game.getItemManager().getActiveItems()) {
            if (item.isActive()) {
                return new AttackItemWord(item.getType().name(), item.getWord(), item.getY());
            }
        }
        return null;
    }

    private static List<ClientFallingWord> toClientWords(ServerGameManager game, Player p) {
        List<ClientFallingWord> words = new ArrayList<>();
        for (FallingWord w : game.getWordManager(p.getPlayerId()).getActiveWords()) {
            words.add(new ClientFallingWord(w.getWordId(), w.getText(), w.getX(), w.getY()));
        }
        return words;
    }
}