package server.game.word;

import server.db.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import common.type.Theme;

public class DBWordRepository implements WordRepository {

    private DatabaseConnector connector;

    // 생성자
    public DBWordRepository() {
        this.connector = new DatabaseConnector();
    }

    // 특정 테마에서 무작위 단어 가져오기
    @Override
    public List<String> getRandomWordsByTheme(int themeId, int count) {

        List<String> words = new ArrayList<>();

        String sql = "SELECT word_text FROM words "
                   + "WHERE theme_id = ? "
                   + "ORDER BY RAND() LIMIT ?";

        try (Connection conn = connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, themeId);
            pstmt.setInt(2, count);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    words.add(rs.getString("word_text"));
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException("단어 조회 실패", e);
        }

        return words;
    }

    // 전체 테마 목록 가져오기
    @Override
    public List<Theme> getThemes() {
        return Arrays.asList(Theme.values());
    }
}