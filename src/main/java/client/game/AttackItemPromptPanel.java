package client.game;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import common.dto.AttackItemWord;

public class AttackItemPromptPanel extends JPanel {
    private static final int PANEL_WIDTH = 180;
    private static final int PANEL_HEIGHT = 400;
    private static final float SERVER_SCREEN_HEIGHT = 400.0f;

    private JPanel topPanel;
    private JLabel titleLabel;
    private AttackItemWord fallingItemWord;

    /** @wbp.parser.constructor */
    public AttackItemPromptPanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setMinimumSize(new Dimension(150, PANEL_HEIGHT));
        setSize(PANEL_WIDTH, PANEL_HEIGHT);
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createLineBorder(new Color(200, 208, 220), 2));

        // 상단: 아이템 타이틀 영역
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 8, 10, 8));
        topPanel.setBackground(new Color(235, 240, 248));

        titleLabel = new JLabel("아이템", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        updateHeaderVisual(false);
    }

    private void updateHeaderVisual(boolean active) {
        if (titleLabel == null) {
            return;
        }

        if (active) {
            titleLabel.setText("아이템 활성!");
            titleLabel.setForeground(new Color(185, 28, 28));
        } else {
            titleLabel.setText("아이템");
            titleLabel.setForeground(new Color(71, 85, 105));
        }
    }

    private String getItemEmoji(String type) {
        if (type == null) {
            return "";
        }
        return switch (type.trim().toUpperCase()) {
            case "BOMB" -> "💣";
            case "HEAL" -> "❤️‍🩹";
            case "SHADOW" -> "🕶️";
            default -> "";
        };
    }

    public void updateFallingItem(AttackItemWord item) {
        Runnable updateTask = () -> {
            this.fallingItemWord = item;
            boolean hasItem = (item != null && item.getText() != null && !item.getText().isBlank());
            updateHeaderVisual(hasItem);
            repaint();
        };

        if (SwingUtilities.isEventDispatchThread()) {
            updateTask.run();
        } else {
            SwingUtilities.invokeLater(updateTask);
        }
    }

    public void clearFallingItem() {
        Runnable clearTask = () -> {
            this.fallingItemWord = null;
            updateHeaderVisual(false);
            repaint();
        };

        if (SwingUtilities.isEventDispatchThread()) {
            clearTask.run();
        } else {
            SwingUtilities.invokeLater(clearTask);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int topOffset = (topPanel != null && topPanel.getHeight() > 0) ? topPanel.getHeight() : 42;
        int centerX = getWidth() / 2;

        // 중앙 가이드 라인
        g2d.setColor(new Color(226, 232, 240));
        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10.0f, new float[]{6.0f, 6.0f}, 0.0f));
        g2d.drawLine(centerX, topOffset + 10, centerX, getHeight() - 16);
        g2d.setStroke(new BasicStroke(1.5f));

        // 1. 활성 아이템 단어가 없을 때 대기 상태 표시
        if (fallingItemWord == null || fallingItemWord.getText() == null || fallingItemWord.getText().isBlank()) {
            g2d.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
            g2d.setColor(new Color(148, 163, 184));
            String waitingText = "아이템 대기 중";
            FontMetrics fm = g2d.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(waitingText)) / 2;
            int ty = (getHeight() + topOffset) / 2;
            g2d.drawString(waitingText, tx, ty);
            g2d.dispose();
            return;
        }

        // 2. 떨어지는 공격 아이템 단어 렌더링
        String text = fallingItemWord.getText();
        g2d.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        FontMetrics fm = g2d.getFontMetrics();

        int padX = 12;
        int padY = 6;
        int boxWidth = Math.min(getWidth() - 16, fm.stringWidth(text) + padX * 2);
        int boxHeight = fm.getHeight() + padY * 2;

        // X는 가로 중앙 정렬, Y는 서버 기준 높이(400) -> 실제 패널 높이에 맞게 비례 변환
        int boxX = (getWidth() - boxWidth) / 2;
        int boxY = (int) ((fallingItemWord.getY() / SERVER_SCREEN_HEIGHT) * getHeight());

        // 외곽 강조 빛 효과
        g2d.setColor(new Color(254, 202, 202, 160));
        g2d.fillRoundRect(boxX - 3, boxY - 3, boxWidth + 6, boxHeight + 6, 14, 14);

        // 아이템 단어 박스 배경
        g2d.setColor(new Color(254, 226, 226));
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 12, 12);

        // 아이템 단어 테두리
        g2d.setColor(new Color(220, 38, 38));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 12, 12);

        // 아이템 단어 텍스트 (박스 정중앙)
        g2d.setColor(new Color(153, 27, 27));
        int textX = boxX + (boxWidth - fm.stringWidth(text)) / 2;
        int textY = boxY + ((boxHeight - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, textX, textY);

        // 3. 박스 위 중앙에 아이템 이모지 렌더링
        String emoji = getItemEmoji(fallingItemWord.getItemtype());
        if (!emoji.isEmpty()) {
            g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            g2d.setColor(new Color(30, 41, 59));
            FontMetrics emojiFm = g2d.getFontMetrics();
            int emojiX = boxX + (boxWidth - emojiFm.stringWidth(emoji)) / 2;
            int emojiY = boxY - 6;
            g2d.drawString(emoji, emojiX, emojiY);
        }

        g2d.dispose();
    }
}