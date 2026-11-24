package server.game.word;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordManager {

    // 필드
    private WordRepository repository;
    private List<String> wordList; // 단어 후보 관리
    private List<FallingWord> activeWords; //현재 낙하중인단어 & 정답 입력 시 해당 객체 제거
    private float fallSpeed;
    private int nextWordId;

    private Random random = new Random();

    // 게임 화면 좌표
    private static final float SCREEN_WIDTH = 300f;
    public static final float SCREEN_HEIGHT =400f;

    // 단계별 바닥 도달 시간 (밀리초)
    private static final long[] FALL_DURATIONS = {
        10000L, 8000L, 6500L, 5000L, 4000L
    };


    // 생성자
    public WordManager(WordRepository repository) {

        this.repository = repository;
        this.wordList = new ArrayList<>();
        this.activeWords = new ArrayList<>();

        this.fallSpeed = 1.0f; // 커질수록 속도 상승... 이거 나중에 30초마다 레벨 올라감
        this.nextWordId = 1; //새롭게 생성되는 단어에 고유한 id 부여 (같은 단어 여러번 등장해도 worldid는 서로 다르다)
    }


    // 1. 테마에 따른 단어 목록 생성(DB에서 목록 가져온다. 호출시점 : 게임시작)
    public void generateWords(int themeId, int count) {

        wordList = new ArrayList<>(
            repository.getRandomWordsByTheme(themeId, count)
        );
    }


    // 2. 단어 생성 및 화면에 추가(단어생성주기마다 생성)
    public FallingWord spawnWord() {

        if (wordList.isEmpty()) {
            return null;
        }

        // 무작위 단어 선택
        int index = random.nextInt(wordList.size());
        String text = wordList.get(index); //단어를 삭제하지 않고 가져오기만한다.

        // 단어 ID 생성
        int wordId = nextWordId++;

        // 생성 위치
        float x = random.nextFloat() * SCREEN_WIDTH;
        float y = 0f;

        // 생성 시각
        long spawnTime = System.currentTimeMillis();

        // 현재 낙하 속도에 따른 도착 시간
        long duration = (long)(10000 / fallSpeed);
        long reachBottomTime = spawnTime + duration;

        FallingWord word = new FallingWord(
            wordId,
            text,
            x,
            y,
            spawnTime,
            reachBottomTime
        );

        activeWords.add(word);

        return word;
    }


    // 3. 플레이어의 단어 입력 처리
    public boolean processWordInput(String text) {

        if (text == null || text.isBlank()) {
            return false;
        }

        long now = System.currentTimeMillis();

        FallingWord targetWord = null;

        for (FallingWord word : activeWords) {

            // 이미 완료했거나 바닥에 도달한 단어는 제외
            if (word.isCompleted() || word.hasReachedBottom(now)) {
                continue;
            }

            // 입력한 텍스트와 일치하는 단어 검색
            if (word.getText().equals(text)) {

                // 동일한 단어가 여러 개라면
                // 바닥 도달 예정 시각이 가장 빠른 단어 선택
                if (targetWord == null ||
                    word.getReachBottomTime() <
                    targetWord.getReachBottomTime()) {

                    targetWord = word;
                }
            }
        }

        // 일치하는 단어가 존재하는 경우
        if (targetWord != null) {

            targetWord.markCompleted();

            activeWords.remove(targetWord);

            return true;
        }

        return false;
    }


    // 4. 단어 위치 갱신
    public void updateWordPositions(long now) {

        for (FallingWord word : activeWords) {

            long spawnTime = word.getSpawnTime();
            long reachBottomTime = word.getReachBottomTime();

            long duration = reachBottomTime - spawnTime;

            if (duration <= 0) {
                continue;
            }

            float progress =
                (float)(now - spawnTime) / duration;

            // 0 ~ 1 사이로 제한
            progress = Math.max(0f, Math.min(1f, progress));

            // y좌표 계산
            float newY = progress * SCREEN_HEIGHT;

            word.setY(newY);
        }
    }


    // 5. 바닥에 도달한 단어 반환 및 제거
    public List<FallingWord> getMissedWords(long now) {

        List<FallingWord> missedWords = new ArrayList<>();

        for (FallingWord word : activeWords) {

            if (!word.isCompleted() &&
                word.hasReachedBottom(now)) {

                missedWords.add(word);
            }
        }

        // 놓친 단어 제거
        activeWords.removeAll(missedWords);

        return missedWords;
    }


    // 6. 현재 낙하 속도 반환
    public float getFallSpeed() {
        return fallSpeed;
    }


    // 7. 속도 단계 변경
    public void setSpeedLevel(int level) {

        if (level < 1 || level > 5) {
            return;
        }

        // 1단계의 낙하 시간을 기준으로 속도 배율 계산
        fallSpeed = 10000f / FALL_DURATIONS[level - 1];
    }


    // 8. 현재 활성화된 단어 목록 반환
    public List<FallingWord> getActiveWords() {

        return new ArrayList<>(activeWords);
    }
    
    // 아이템 생성에 사용할 랜덤 단어 반환
    public String getRandomWord() {

        if (wordList.isEmpty()) {
            return null;
        }

        int index = random.nextInt(wordList.size());

        return wordList.get(index);
    }
}