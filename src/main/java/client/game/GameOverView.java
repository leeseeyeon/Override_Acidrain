package client.game;

import javax.swing.*;
import java.awt.event.ActionListener;

public class GameOverView extends JFrame {
    private ResultScreenStrategy screenStrategy;
    private GameResult gameResult;
    private JButton restartButton;
    private JButton mainMenuButton;

    public GameOverView() {
        // TODO: 종료 화면 UI 컴포넌트 초기화
    }

    public void setScreenStrategy(ResultScreenStrategy strategy) {
        // TODO: 승리/패배 전략 설정
        this.screenStrategy = strategy;
    }

    public void displayResult(GameResult result) {
        // TODO: 결과 데이터를 바탕으로 화면에 승패 내용 반영
    }

    public void setRestartButtonListener(ActionListener listener) {
        // TODO: 재시작 버튼 이벤트 연결
    }

    public void setMainMenuButtonListener(ActionListener listener) {
        // TODO: 메인 메뉴 버튼 이벤트 연결
    }
}