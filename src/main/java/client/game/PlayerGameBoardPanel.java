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
import javax.swing.border.EmptyBorder;

public class PlayerGameBoardPanel extends JPanel {
    private static final int BOARD_WIDTH = 350;
    private static final int BOARD_HEIGHT = 400;

    private JLabel playerNameLabel;
    private JProgressBar hpGaugeBar;
    private List<ClientFallingWord> fallingWords;
    private JTextField wordInputField;
    private boolean isShadowEffectActive;

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
        bottomPanel.add(wordInputField, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void updatePlayerStatus(PlayerStateData state) {
        if (state == null) {
            return;
        }

        if (state.getNickname() != null) {
            playerNameLabel.setText(state.getNickname());
        }

        int maxHp = state.getMaxHp() > 0 ? state.getMaxHp() : 100;
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
        String word = wordInputField.getText().trim();
        clearInputField();
        return word;
    }

    public void clearInputField() {
        wordInputField.setText("");
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

                boolean isAttackItem = (word instanceof AttackItemWord);
                int padX = 8;
                int padY = 4;
                int boxWidth = fm.stringWidth(text) + padX * 2;
                int boxHeight = fm.getHeight() + padY * 2;

                // 단어 배경 박스
                g2d.setColor(isAttackItem ? new Color(254, 226, 226) : Color.WHITE);
                g2d.fillRoundRect(x, y - fm.getAscent() - padY, boxWidth, boxHeight, 10, 10);

                // 단어 테두리
                g2d.setColor(isAttackItem ? new Color(220, 38, 38) : new Color(100, 116, 139));
                g2d.drawRoundRect(x, y - fm.getAscent() - padY, boxWidth, boxHeight, 10, 10);

                // 단어 텍스트
                g2d.setColor(isAttackItem ? new Color(185, 28, 28) : new Color(15, 23, 42));
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
}