package server.db;

import server.game.word.DBWordRepository;
import server.game.word.WordRepository;
import server.game.word.WordManager;
import server.game.word.FallingWord;
import common.message.Theme;

public class DBConnectionTest {

    public static void main(String[] args) {

        WordRepository repository = new DBWordRepository();

        WordManager manager = new WordManager(repository);

        // 컴퓨터 용어 테마의 단어 목록 가져오기
        manager.generateWords(Theme.CS.getId(), 100);

        // 단어 10개 생성 테스트
        for (int i = 0; i < 10; i++) {

            FallingWord word = manager.spawnWord();

            if (word != null) {
                System.out.println(
                    word.getWordId() + " : " + word.getText()
                );
            }
        }
    }
}