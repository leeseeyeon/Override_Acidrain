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
	
	//데이터베이스 연결을 담당하는 객체
    private DatabaseConnector connector;

    // DB연결 객체 초기화
    public DBWordRepository() {
        this.connector = new DatabaseConnector();
    }

    // 특정 테마에 해당하는 단어 중 지정된 개수만큼 무작위로 조회
    @Override
    public List<String> getRandomWordsByTheme(int themeId, int count) {
    	
    	//조회한 단어 저장할 리스트
        List<String> words = new ArrayList<>();
        
        //테마 ID로 단어 필터링, 무작위로 정렬 후 지정된 개수만큼 조회
        String sql = "SELECT word_text FROM words "
                   + "WHERE theme_id = ? "
                   + "ORDER BY RAND() LIMIT ?";

        try (Connection conn = connector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	
        	//SQL첫번째 매개변수에 테마 ID설정
            pstmt.setInt(1, themeId);
            
            //SQL두번째 매개변수에 조회할 단어 개수 설정
            pstmt.setInt(2, count);
            
            //SQL실행 후 조회 결과를 ResultSet으로 반환
            try (ResultSet rs = pstmt.executeQuery()) {

            	//조회 결과에서 단어를 하나씩 가져와 리스트에 저장
                while (rs.next()) {
                    words.add(rs.getString("word_text"));
                }
            }

        } catch (SQLException e) {
        	//DB조회 과정에서 오류가 발생하면 예외 전달
            throw new IllegalStateException("단어 조회 실패", e);
        }

        //조회한 단어 목록 반환
        return words;
    }

    // 전체 테마 목록 가져오기(Thmem enum에 정의되어있음)
    @Override
    public List<Theme> getThemes() {
        return Arrays.asList(Theme.values());
    }
}