package server.game.word;
import common.message.Theme;


import java.util.List;

public interface WordRepository {

    // 특정 테마에서 무작위 단어 가져오기
    List<String> getRandomWordsByTheme(int themeId, int count);

    // 전체 테마 목록 가져오기
    List<Theme> getThemes();
}