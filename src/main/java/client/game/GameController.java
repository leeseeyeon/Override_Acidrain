package client.game;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameController implements ActionListener, GameModelListener {
    private GameClientModel gameModel;
    private GameView gameView;
    private GameOverView gameOverView;
    private ServerConnection serverConnection;

    public GameController(GameClientModel model, GameView view, ServerConnection connection) {
        this.gameModel = model;
        this.gameView = view;
        this.serverConnection = connection;
        // TODO: 리스너 등록 및 초기 설정
    }

    public void onWordSubmitted(String word) {
        // TODO: 사용자가 단어를 입력하고 엔터 쳤을 때 서버로 전송하는 로직
    }

    public void onGameStateReceived(GameStateUpdate update) {
        // TODO: 서버로부터 게임 상태 업데이트를 받았을 때 모델에 반영
    }

    public void onGameFinished(GameResult result) {
        // TODO: 게임 종료 시 결과 처리 및 화면 전환
    }

    public void onClickRestart() {
        // TODO: 재시작 버튼 클릭 시 처리
    }

    public void onClickMainMenu() {
        // TODO: 메인 메뉴로 돌아가기 버튼 클릭 시 처리
    }

    public void startClientGameLoop() {
        // TODO: 클라이언트 내부 타이머 혹은 루프를 돌며 단어 낙하(moveWord) 등 주기적 처리
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: 버튼 클릭 등 액션 이벤트 처리
    }

    @Override
    public void onGameStateChanged(GameClientModel model) {
        // TODO: 모델 변경 감지 처리
    }
}