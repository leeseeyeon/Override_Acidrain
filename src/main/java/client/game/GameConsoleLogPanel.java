package client.game;

import javax.swing.*;
import java.awt.*;

public class GameConsoleLogPanel extends JPanel {
    private JTextArea logTextArea;
    private JScrollPane scrollPane;

    public GameConsoleLogPanel() {
        // TODO: JTextArea와 JScrollPane 생성 후 패널에 부착
    }

    public void appendLogMessage(String message) {
        // TODO: 로그 영역에 새로운 메시지 추가 및 스크롤 맨 아래로 내리기
    }

    public void clearConsoleLog() {
        // TODO: 로그 텍스트 초기화
        if (logTextArea != null) {
            logTextArea.setText("");
        }
    }
}