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

public class GameConsoleLogPanel extends JPanel {
    private static final int DEFAULT_WIDTH = 700;
    private static final int DEFAULT_HEIGHT = 120;

    private JTextArea logTextArea;
    private JScrollPane scrollPane;

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

        // 로그 출력 영역
        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);
        logTextArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        logTextArea.setBackground(new Color(30, 41, 59));
        logTextArea.setForeground(new Color(226, 232, 240));
        logTextArea.setMargin(new Insets(6, 10, 6, 10));

        // 스크롤 패널 부착
        scrollPane = new JScrollPane(logTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void appendLogMessage(String message) {
        if (logTextArea == null || message == null || message.isBlank()) {
            return;
        }

        Runnable appendTask = () -> {
            logTextArea.append(message + "\n");
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        };

        if (SwingUtilities.isEventDispatchThread()) {
            appendTask.run();
        } else {
            SwingUtilities.invokeLater(appendTask);
        }
    }

    public void updateLogs(List<String> logs) {
        if (logTextArea == null) {
            return;
        }

        Runnable updateTask = () -> {
            if (logs == null || logs.isEmpty()) {
                logTextArea.setText("");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (String log : logs) {
                if (log != null && !log.isBlank()) {
                    sb.append(log).append("\n");
                }
            }
            logTextArea.setText(sb.toString());
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        };

        if (SwingUtilities.isEventDispatchThread()) {
            updateTask.run();
        } else {
            SwingUtilities.invokeLater(updateTask);
        }
    }
    
    public void appendLog(String message) {
        appendLogMessage(message);
    }

    public void clearConsoleLog() {
        if (logTextArea == null) {
            return;
        }

        Runnable clearTask = () -> logTextArea.setText("");

        if (SwingUtilities.isEventDispatchThread()) {
            clearTask.run();
        } else {
            SwingUtilities.invokeLater(clearTask);
        }
    }
}