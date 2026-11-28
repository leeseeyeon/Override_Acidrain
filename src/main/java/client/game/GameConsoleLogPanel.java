package client.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

// 게임 진행 중 발생하는 이벤트 로그를 하단에 표시하는 콘솔 패널
public class GameConsoleLogPanel extends JPanel {
    private static final int DEFAULT_WIDTH = 700;
    private static final int DEFAULT_HEIGHT = 120;

    private JTextArea logTextArea;

    public GameConsoleLogPanel() {
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(235, 240, 248));
        setBorder(BorderFactory.createLineBorder(new Color(200, 208, 220), 2));

        // 상단 헤더 라벨
        JLabel titleLabel = new JLabel("게임 로그");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        titleLabel.setForeground(new Color(30, 41, 59));
        titleLabel.setBorder(new EmptyBorder(4, 10, 4, 10));
        add(titleLabel, BorderLayout.NORTH);

        // 로그 출력 영역 (읽기 전용, 자동 줄바꿈)
        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);
        logTextArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        logTextArea.setBackground(new Color(30, 41, 59));
        logTextArea.setForeground(new Color(226, 232, 240));
        logTextArea.setMargin(new Insets(6, 10, 6, 10));

        JScrollPane scrollPane = new JScrollPane(logTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    // 서버에서 받은 전체 로그 목록으로 갱신하고 최신 로그(맨 아래)로 스크롤 이동
    public void updateLogs(List<String> logs) {
        if (logTextArea == null || logs == null) {
            return;
        }

        runOnEdt(() -> {
            StringBuilder sb = new StringBuilder();
            for (String log : logs) {
                if (log != null && !log.isBlank()) {
                    sb.append(log).append("\n");
                }
            }
            logTextArea.setText(sb.toString());
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        });
    }

    // 게임 재시작·초기화 시 콘솔 로그 영역 비우기
    public void clearConsoleLog() {
        if (logTextArea == null) {
            return;
        }
        runOnEdt(() -> logTextArea.setText(""));
    }

    // Swing UI 갱신을 EDT(Event Dispatch Thread)에서 안전하게 실행
    private void runOnEdt(Runnable task) {
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }
}