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

import common.config.GameConfig;
import common.dto.ClientFallingWord;
import common.dto.PlayerStateData;

// 개별 플레이어의 상태(닉네임, HP), 낙하 단어, 입력창 및 시야 차단 효과를 렌더링하는 보드 패널
public class PlayerGameBoardPanel extends JPanel {
	private static final int BOARD_WIDTH = 350;
    private static final int BOARD_HEIGHT = 400;
    private static final float SERVER_SCREEN_HEIGHT = GameConfig.FIELD_HEIGHT;
    private static final int SHADOW_DURATION_MS = 4500;
    private static final int MAX_HP = GameConfig.MAX_HP;

    private static final Color PANEL_BG_COLOR = new Color(248, 250, 252);
    private static final Color PANEL_BORDER_COLOR = new Color(200, 208, 220);
    private static final Color SUB_PANEL_BG_COLOR = new Color(235, 240, 248);
    private static final Color PLAYER_NAME_COLOR = new Color(30, 41, 59);
    private static final Color HP_BG_COLOR = new Color(226, 232, 240);
    private static final Color HP_HIGH_COLOR = new Color(46, 204, 113);
    private static final Color HP_MID_COLOR = new Color(243, 156, 18);
    private static final Color HP_LOW_COLOR = new Color(231, 76, 60);

    private static final Color WORD_BORDER_COLOR = new Color(100, 116, 139);
    private static final Color WORD_TEXT_COLOR = new Color(15, 23, 42);
    private static final Color SHADOW_BG_COLOR = new Color(15, 23, 42, 255);
    private static final Color SHADOW_TEXT_COLOR = new Color(226, 232, 240);

    private static final Font NAME_FONT = new Font("맑은 고딕", Font.BOLD, 14);
    private static final Font INPUT_FONT = new Font("맑은 고딕", Font.PLAIN, 14);
    private static final Font WORD_FONT = new Font("맑은 고딕", Font.BOLD, 14);
    private static final Font SHADOW_FONT = new Font("맑은 고딕", Font.BOLD, 16);

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
        setBackground(PANEL_BG_COLOR);
        setBorder(BorderFactory.createLineBorder(PANEL_BORDER_COLOR, 2));

        // 상단: 닉네임 및 HP 게이지
        JPanel topPanel = new JPanel(new BorderLayout(0, 6));
        topPanel.setBorder(new EmptyBorder(8, 10, 8, 10));
        topPanel.setBackground(SUB_PANEL_BG_COLOR);

        playerNameLabel = new JLabel("Player");
        playerNameLabel.setFont(NAME_FONT);
        playerNameLabel.setForeground(PLAYER_NAME_COLOR);
        topPanel.add(playerNameLabel, BorderLayout.NORTH);

        hpGaugeBar = new JProgressBar(0, MAX_HP);
        hpGaugeBar.setValue(MAX_HP);
        hpGaugeBar.setStringPainted(true);
        hpGaugeBar.setString(MAX_HP + " / " + MAX_HP);
        hpGaugeBar.setPreferredSize(new Dimension(0, 20));
        hpGaugeBar.setForeground(HP_HIGH_COLOR);
        hpGaugeBar.setBackground(HP_BG_COLOR);
        topPanel.add(hpGaugeBar, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // 하단: 단어 입력창 (중앙 영역은 paintComponent에서 낙하 단어 렌더링)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(8, 10, 8, 10));
        bottomPanel.setBackground(SUB_PANEL_BG_COLOR);

        wordInputField = new JTextField();
        wordInputField.setFont(INPUT_FONT);
        wordInputField.setPreferredSize(new Dimension(0, 30));
        initTypingTimer();
        bottomPanel.add(wordInputField, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 첫 글자 입력 시점부터 타이핑 경과 시간 측정 시작
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

    // 플레이어 닉네임과 HP 게이지 갱신
    public void updatePlayerStatus(PlayerStateData state) {
        if (state == null) {
            return;
        }

        this.id = state.getplayerId();

        if (state.getNickname() != null) {
            this.nickname = state.getNickname();
            playerNameLabel.setText(state.getNickname());
        }

        int currentHp = Math.max(0, Math.min(state.getCurrentHp(), MAX_HP));
        hpGaugeBar.setMaximum(MAX_HP);
        hpGaugeBar.setValue(currentHp);
        hpGaugeBar.setString(currentHp + " / " + MAX_HP);
        hpGaugeBar.setForeground(getHpColor((double) currentHp / MAX_HP));
    }

    private Color getHpColor(double ratio) {
        if (ratio > 0.5) {
            return HP_HIGH_COLOR;
        } else if (ratio > 0.25) {
            return HP_MID_COLOR;
        }
        return HP_LOW_COLOR;
    }

    // 서버에서 받은 낙하 단어 목록을 반영하고 화면 갱신
    public void updateFallingWords(List<ClientFallingWord> words) {
        this.fallingWords = (words == null) ? new ArrayList<>() : new ArrayList<>(words);
        repaint();
    }

    public String getEnteredWord() {
        return wordInputField.getText().trim();
    }

    // 입력 시작부터 제출까지의 경과 시간(초) 반환 후 타이머 초기화
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

    // 기존 리스너를 제거한 뒤 단일 단어 제출 리스너 등록
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

        drawFallingWords(g2d);

        // 상단 상태바·하단 입력창은 자식 컴포넌트라 오버레이에 가려지지 않음
        if (isShadowEffectActive) {
            drawShadowOverlay(g2d);
        }

        g2d.dispose();
    }

    // 서버 기준 Y 좌표를 현재 패널 높이에 비례 변환해 낙하 단어 렌더링
    private void drawFallingWords(Graphics2D g2d) {
        if (fallingWords == null || fallingWords.isEmpty()) {
            return;
        }

        g2d.setFont(WORD_FONT);
        FontMetrics fm = g2d.getFontMetrics();
        int padX = 8;
        int padY = 4;

        for (ClientFallingWord word : fallingWords) {
            if (word == null || word.getText() == null) {
                continue;
            }

            String text = word.getText();
            int x = (int) word.getX();
            int y = (int) ((word.getY() / SERVER_SCREEN_HEIGHT) * getHeight());

            int boxWidth = fm.stringWidth(text) + padX * 2;
            int boxHeight = fm.getHeight() + padY * 2;
            int boxY = y - fm.getAscent() - padY;

            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(x, boxY, boxWidth, boxHeight, 10, 10);

            g2d.setColor(WORD_BORDER_COLOR);
            g2d.drawRoundRect(x, boxY, boxWidth, boxHeight, 10, 10);

            g2d.setColor(WORD_TEXT_COLOR);
            g2d.drawString(text, x + padX, y);
        }
    }

    // 시야 차단 효과 활성화 시 보드 전체를 가리는 오버레이 렌더링
    private void drawShadowOverlay(Graphics2D g2d) {
        g2d.setColor(SHADOW_BG_COLOR);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setFont(SHADOW_FONT);
        g2d.setColor(SHADOW_TEXT_COLOR);
        String msg = "시야 차단 효과 적용 중";
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(msg)) / 2;
        int textY = getHeight() / 2;
        g2d.drawString(msg, textX, textY);
    }

    public int getplayerId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    // 기본 지속 시간(4.5초) 동안 시야 차단 효과 발동
    public void triggerShadowEffect() {
        triggerShadowEffect(SHADOW_DURATION_MS);
    }

    // 지정 시간(ms) 동안 시야 차단 효과 발동 (중복 피격 시 지속 시간 초기화)
    public void triggerShadowEffect(int durationMs) {
        runOnEdt(() -> {
            stopShadowTimer();
            isShadowEffectActive = true;
            repaint();

            shadowTimer = new Timer(durationMs, e -> {
                isShadowEffectActive = false;
                repaint();
                ((Timer) e.getSource()).stop();
            });
            shadowTimer.setRepeats(false);
            shadowTimer.start();
        });
    }

    // 게임 종료·재시작 시 시야 차단 효과 즉시 해제
    public void clearShadowEffect() {
        runOnEdt(() -> {
            stopShadowTimer();
            isShadowEffectActive = false;
            repaint();
        });
    }

    private void stopShadowTimer() {
        if (shadowTimer != null && shadowTimer.isRunning()) {
            shadowTimer.stop();
        }
    }

    private void runOnEdt(Runnable task) {
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }
}