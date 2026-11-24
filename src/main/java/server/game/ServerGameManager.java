package server.game;

import java.util.List;

import common.GameResult;
import common.GameStatus;
import common.message.Theme;

import server.game.session.GameSession;
import server.game.player.Player;
import server.game.word.WordManager;
import server.game.word.WordRepository;
import server.game.word.FallingWord;
import server.game.item.ItemManager;
import server.game.rule.WinCondition;
import server.game.rule.HpZeroWinCondition;

public class ServerGameManager {

    // 필드
    private GameSession session;

    private WordManager player1WordManager;
    private WordManager player2WordManager;

    private long gameStartTime;
    private int speedLevel;

    private ItemManager itemManager;
    private WinCondition winCondition;

    // 단어 생성 시간 관리
    private long lastSpawnTime;

    // 최종 게임 결과
    private GameResult gameResult;

    // 게임 설정값
    private static final int MISS_DAMAGE = 10;
    private static final int WORD_COUNT = 100;
    private static final int MAX_SPEED_LEVEL = 5;

    private static final long SPAWN_INTERVAL = 2000L;
    private static final long SPEED_INTERVAL = 30000L;

    private static final double ITEM_PROBABILITY = 0.1;

    // 생성자
    public ServerGameManager(int gameId,
            Player player1, Player player2,
            Theme theme, WordRepository repository) {

        this.session = new GameSession(
                gameId, player1, player2, theme);

        // 플레이어별 WordManager 생성
        this.player1WordManager = new WordManager(repository);
        this.player2WordManager = new WordManager(repository);

        // 두 플레이어가 공유하는 아이템 관리자
        this.itemManager = new ItemManager(
                session, ITEM_PROBABILITY);

        this.winCondition = new HpZeroWinCondition();

        this.speedLevel = 1;
        this.gameResult = null;
    }


    // 1. 게임 시작
    public synchronized void startGame() {

        if (session.getStatus() != GameStatus.READY) {
            return;
        }

        // 대기실에서 선택한 테마 ID 가져오기
        int themeId = session.getTheme().getId();

        // 테마에 따른 단어 목록 생성
        player1WordManager.generateWords(themeId, WORD_COUNT);
        player2WordManager.generateWords(themeId, WORD_COUNT);

        // 초기 속도 설정
        speedLevel = 1;

        player1WordManager.setSpeedLevel(speedLevel);
        player2WordManager.setSpeedLevel(speedLevel);

        // 시작 시간 기록
        gameStartTime = System.currentTimeMillis();
        lastSpawnTime = gameStartTime;

        session.setStartTime(gameStartTime);
        session.setStatus(GameStatus.PLAYING);

        // 첫 번째 단어 생성
        player1WordManager.spawnWord();
        player2WordManager.spawnWord();
    }


    // 2. 게임 진행 시간에 따른 속도 변경
    public synchronized void updateSpeedLevel(long now) {

        if (session.getStatus() != GameStatus.PLAYING) {
            return;
        }

        long elapsedTime = now - gameStartTime;

        // 30초마다 레벨 증가
        int newLevel = 1 + (int)(elapsedTime / SPEED_INTERVAL);

        // 최대 5단계로 제한
        newLevel = Math.min(newLevel, MAX_SPEED_LEVEL);

        if (newLevel != speedLevel) {

            speedLevel = newLevel;

            player1WordManager.setSpeedLevel(speedLevel);
            player2WordManager.setSpeedLevel(speedLevel);
        }
    }


    // 3. 플레이어별 WordManager 반환
    public WordManager getWordManager(int playerId) {

        if (session.getPlayer1().getPlayerId() == playerId) {
            return player1WordManager;
        }

        if (session.getPlayer2().getPlayerId() == playerId) {
            return player2WordManager;
        }

        return null;
    }


    // 4. 일반 단어 입력 처리
    public synchronized void processInput(
            int playerId, int wordId,
            String word, double typingTime) {

        if (session.getStatus() != GameStatus.PLAYING) {
            return;
        }

        Player player = session.getPlayer(playerId);
        WordManager wordManager = getWordManager(playerId);

        if (player == null || wordManager == null) {
            return;
        }

        if (word == null || !Double.isFinite(typingTime)
                || typingTime <= 0) {
            return;
        }

        // 단어 입력 성공 여부 확인
        boolean isCorrect =
                wordManager.processWordInput(wordId, word);

        // 타수 및 정확도 기록
        player.getTypingStats().recordInput(
                word, typingTime, isCorrect);

        checkGameOver();
    }


    // 5. 아이템 단어 입력 처리
    public synchronized void processItemInput(
            int playerId, int itemId,
            String word, double typingTime) {

        if (session.getStatus() != GameStatus.PLAYING) {
            return;
        }

        Player player = session.getPlayer(playerId);

        if (player == null) {
            return;
        }

        if (word == null || !Double.isFinite(typingTime)
                || typingTime <= 0) {
            return;
        }

        // 아이템 획득 및 효과 적용
        boolean isCorrect = itemManager.processItemInput(
                playerId, itemId, word);

        // 타수 및 정확도 기록
        player.getTypingStats().recordInput(
                word, typingTime, isCorrect);

        // 아이템 효과로 HP가 0이 되었는지 확인
        checkGameOver();
    }


    // 6. 게임 상태 업데이트
    public synchronized void updateGame() {

        if (session.getStatus() != GameStatus.PLAYING) {
            return;
        }

        long now = System.currentTimeMillis();

        // 속도 레벨 변경
        updateSpeedLevel(now);

        // 단어 위치 갱신
        player1WordManager.updateWordPositions(now);
        player2WordManager.updateWordPositions(now);

        // 바닥에 도달한 단어 가져오기 및 제거
        List<FallingWord> missedWords1 =
                player1WordManager.getMissedWords(now);

        List<FallingWord> missedWords2 =
                player2WordManager.getMissedWords(now);

        // 놓친 단어에 따른 HP 감소
        session.getPlayer1().decreaseHP(
                missedWords1.size() * MISS_DAMAGE);

        session.getPlayer2().decreaseHP(
                missedWords2.size() * MISS_DAMAGE);

        // 만료된 아이템 제거
        itemManager.removeExpiredItems(now);

        // 게임 종료 여부 확인
        if (checkGameOver()) {
            return;
        }

        // 일정 시간마다 단어 생성
        if (now - lastSpawnTime >= SPAWN_INTERVAL) {

            player1WordManager.spawnWord();
            player2WordManager.spawnWord();

            // 아이템 생성 시도
            spawnItem();

            lastSpawnTime = now;
        }
    }


    // 7. 아이템 생성
    private void spawnItem() {

        // 현재 활성화된 아이템이 있으면 생성하지 않음
        if (!itemManager.getActiveItems().isEmpty()) {
            return;
        }

        String word = player1WordManager.getRandomWord();

        if (word == null) {
            return;
        }

        // 현재 속도에 맞춰 확률적으로 아이템 생성
        itemManager.spawnRandomItem(
                word, player1WordManager.getFallSpeed());
    }


    // 8. 최종 게임 통계 계산
    public synchronized GameResult calculateFinalStats() {

        Player player1 = session.getPlayer1();
        Player player2 = session.getPlayer2();
        Player winner = session.getWinner();

        if (winner == null) {
            return null;
        }

        return new GameResult(
                winner.getPlayerId(),

                player1.getTypingStats().calculateCPM(),
                player2.getTypingStats().calculateCPM(),

                player1.getTypingStats().calculateAccuracy(),
                player2.getTypingStats().calculateAccuracy()
        );
    }


    // 9. 게임 종료 여부 확인
    public synchronized boolean checkGameOver() {

        if (session.getStatus() == GameStatus.FINISHED) {
            return true;
        }

        if (session.getStatus() != GameStatus.PLAYING) {
            return false;
        }

        Player winner = winCondition.determineWinner(session);

        if (winner != null) {

            session.setWinner(winner);

            endGame();

            return true;
        }

        return false;
    }


    // 10. 게임 종료
    public synchronized void endGame() {

        if (session.getStatus() == GameStatus.FINISHED
                || session.getWinner() == null) {
            return;
        }

        // 종료 시간 기록
        session.setEndTime(System.currentTimeMillis());

        // 최종 게임 결과 계산
        gameResult = calculateFinalStats();

        // 게임 종료 상태로 변경
        session.setStatus(GameStatus.FINISHED);
    }


    // Getter
    public GameSession getSession() {
        return session;
    }

    public WordManager getPlayer1WordManager() {
        return player1WordManager;
    }

    public WordManager getPlayer2WordManager() {
        return player2WordManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public int getSpeedLevel() {
        return speedLevel;
    }

    public long getGameStartTime() {
        return gameStartTime;
    }

    public synchronized GameResult getGameResult() {
        return gameResult;
    }
}