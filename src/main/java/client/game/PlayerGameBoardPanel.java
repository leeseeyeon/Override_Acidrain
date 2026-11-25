package client.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import common.message.game.ClientFallingWord;
import common.message.game.PlayerStateData;

public class PlayerGameBoardPanel extends JPanel {
    private static final int BOARD_WIDTH = 350;
    private static final int BOARD_HEIGHT = 400;
    private static final int SHADOW_DURATION_MS = 4500; // 4.5초

    private JLabel playerNameLabel;
    private JProgressBar hpGaugeBar;
    private List<ClientFallingWord> fallingWords;
    private JTextField wordInputField;

    private int id = -1;
    private String nickname = "Player";
    private long typingStartTimeMs = 0L;

    private boolean isShadowEffectActive = false;
    private Timer shadowTimer;

    /** @wbp.parser.constructor */
    public PlayerGameBoardPanel() {
        this.fallingWords = new ArrayList<>();
        this.isShadowEffectActive = false;

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setMinimumSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setSize(BOARD_WIDTH, BOARD_HEIGHT);
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createLineBorder(new Color(200, 208, 220), 2));

        // 상단: 플레이어 닉네임 및 HP 게이지 영역
        JPanel topPanel = new JPanel(new BorderLayout(0, 6));
        topPanel.setBorder(new EmptyBorder(8, 10, 8, 10));
        topPanel.setBackground(new Color(235, 240, 248));

        playerNameLabel = new JLabel("Player");
        playerNameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        playerNameLabel.setForeground(new Color(30, 41, 59));
        topPanel.add(playerNameLabel, BorderLayout.NORTH);

        hpGaugeBar = new JProgressBar(0, 100);
        hpGaugeBar.setValue(100);
        hpGaugeBar.setStringPainted(true);
        hpGaugeBar.setString("100 / 100");
        hpGaugeBar.setPreferredSize(new Dimension(0, 20));
        hpGaugeBar.setForeground(new Color(46, 204, 113));
        hpGaugeBar.setBackground(new Color(226, 232, 240));
        topPanel.add(hpGaugeBar, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // 하단: 단어 입력 영역 (중앙은 비워두어 paintComponent에서 낙하 단어를 직접 그림)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(8, 10, 8, 10));
        bottomPanel.setBackground(new Color(235, 240, 248));

        wordInputField = new JTextField();
        wordInputField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        wordInputField.setPreferredSize(new Dimension(0, 30));
        initTypingTimer();
        bottomPanel.add(wordInputField, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void initTypingTimer() {
        wordInputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (typingStartTimeMs == 0L && !wordInputField.getText().isEmpty()) {
                    typingStartTimeMs = System.currentTimeMillis();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (wordInputField.getText().isEmpty()) {
                    typingStartTimeMs = 0L;
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
    }

    public void updatePlayerStatus(PlayerStateData state) {
        if (state == null) {
            return;
        }

        this.id = state.getplayerId();

        if (state.getNickname() != null) {
            this.nickname = state.getNickname();
            playerNameLabel.setText(state.getNickname());
        }

        int maxHp = 100;
        int currentHp = Math.max(0, Math.min(state.getCurrentHp(), maxHp));

        hpGaugeBar.setMaximum(maxHp);
        hpGaugeBar.setValue(currentHp);
        hpGaugeBar.setString(currentHp + " / " + maxHp);

        double ratio = (double) currentHp / maxHp;
        if (ratio > 0.5) {
            hpGaugeBar.setForeground(new Color(46, 204, 113)); // 녹색
        } else if (ratio > 0.25) {
            hpGaugeBar.setForeground(new Color(243, 156, 18)); // 주황색
        } else {
            hpGaugeBar.setForeground(new Color(231, 76, 60));  // 빨간색
        }
    }

    public void updateFallingWords(List<ClientFallingWord> words) {
        if (words == null) {
            this.fallingWords = new ArrayList<>();
        } else {
            this.fallingWords = new ArrayList<>(words);
        }
        repaint();
    }

    public String getEnteredWord() {
        return wordInputField.getText().trim();
    }

    // 입력 시작~제출까지의 경과 시간 반환 (초 단위, 서버가 ms 단위면 / 1000.0 제거)
    public double consumeTypingTime() {
        if (typingStartTimeMs == 0L) {
            return 0.0;
        }
        long elapsedMs = System.currentTimeMillis() - typingStartTimeMs;
        typingStartTimeMs = 0L;
        return elapsedMs / 1000.0;
    }

    public void clearInputField() {
        wordInputField.setText("");
        typingStartTimeMs = 0L;
    }

    public void setInputEditable(boolean editable) {
        wordInputField.setEditable(editable);
        wordInputField.setEnabled(editable);
    }

    public void setShadowEffectActive(boolean active) {
        this.isShadowEffectActive = active;
        repaint();
    }

    public boolean isShadowEffectActive() {
        return isShadowEffectActive;
    }

    public void addWordSubmitListener(ActionListener listener) {
        for (ActionListener old : wordInputField.getActionListeners()) {
            wordInputField.removeActionListener(old);
        }
        wordInputField.addActionListener(listener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. 떨어지는 단어 렌더링
        if (fallingWords != null && !fallingWords.isEmpty()) {
            g2d.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();

            for (ClientFallingWord word : fallingWords) {
                if (word == null || word.getText() == null) {
                    continue;
                }

                String text = word.getText();
                int x = (int) word.getX();
                int y = (int) word.getY();

                int padX = 8;
                int padY = 4;
                int boxWidth = fm.stringWidth(text) + padX * 2;
                int boxHeight = fm.getHeight() + padY * 2;

                // 단어 배경 박스
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(x, y - fm.getAscent() - padY, boxWidth, boxHeight, 10, 10);

                // 단어 테두리
                g2d.setColor(new Color(100, 116, 139));
                g2d.drawRoundRect(x, y - fm.getAscent() - padY, boxWidth, boxHeight, 10, 10);

                // 단어 텍스트
                g2d.setColor(new Color(15, 23, 42));
                g2d.drawString(text, x + padX, y);
            }
        }

        // 2. 그림자 방해 효과 렌더링 (상단 상태바·하단 입력창은 자식 컴포넌트라 가려지지 않음)
        if (isShadowEffectActive) {
            g2d.setColor(new Color(15, 23, 42, 220));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setFont(new Font("맑은 고딕", Font.BOLD, 16));
            g2d.setColor(new Color(226, 232, 240));
            String msg = "시야 차단 효과 적용 중";
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(msg)) / 2;
            int textY = getHeight() / 2;
            g2d.drawString(msg, textX, textY);
        }

        g2d.dispose();
    }

    public int getplayerId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    // 기본 4.5초 동안 Shadow 발동
    public void triggerShadowEffect() {
        triggerShadowEffect(SHADOW_DURATION_MS);
    }

    // 지정 시간(ms) 동안 Shadow 발동 (중복 피격 시 시간 초기화)
    public void triggerShadowEffect(int durationMs) {
        Runnable task = () -> {
            if (shadowTimer != null && shadowTimer.isRunning()) {
                shadowTimer.stop();
            }

            isShadowEffectActive = true;
            repaint();

            shadowTimer = new Timer(durationMs, e -> {
                isShadowEffectActive = false;
                repaint();
                ((Timer) e.getSource()).stop();
            });
            shadowTimer.setRepeats(false);
            shadowTimer.start();
        };

        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    // 게임 종료·재시작 시 즉시 해제
    public void clearShadowEffect() {
        Runnable task = () -> {
            if (shadowTimer != null && shadowTimer.isRunning()) {
                shadowTimer.stop();
            }
            isShadowEffectActive = false;
            repaint();
        };

        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }
}