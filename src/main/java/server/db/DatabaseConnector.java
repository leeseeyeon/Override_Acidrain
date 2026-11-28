package server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//데이터베이스 연결관리
public class DatabaseConnector {

	//DB접속 정보를 코드에 직접 노출하지 않도록 환경변수에서 불러온다.
	private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASS = System.getenv("DB_PASSWORD");

    //DB연결을 생성하고 Connection 객체 반환
    //연결 실패 시 SQLException을 호출한 곳으로 전달
    public Connection getConnection() throws SQLException {

        return DriverManager.getConnection(URL, USER, PASS);
    }
}
