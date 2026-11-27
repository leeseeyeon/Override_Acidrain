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

// 두 플레이어 보드 중앙에서 공통 아이템 단어와 이모지를 낙하 형태로 렌더링하는 패널
public class ItemWordPanel extends JPanel {
    private static final int PANEL_WIDTH = 180;
    private static final int PANEL_HEIGHT = 400;
    private static final int MIN_PANEL_WIDTH = 150;
    private static final float SERVER_SCREEN_HEIGHT = 400.0f;

    private static final Color PANEL_BG_COLOR = new Color(248, 250, 252);
    private static final Color PANEL_BORDER_COLOR = new Color(200, 208, 220);
    private static final Color TOP_PANEL_BG_COLOR = new Color(235, 240, 248);
    private static final Color ACTIVE_TITLE_COLOR = new Color(185, 28, 28);
    private static final Color INACTIVE_TITLE_COLOR = new Color(71, 85, 105);

    private static final Font TITLE_FONT = new Font("맑은 고딕", Font.BOLD, 13);
    private static final Font WAITING_FONT = new Font("맑은 고딕", Font.PLAIN, 12);
    private static final Font WORD_FONT = new Font("맑은 고딕", Font.BOLD, 15);
    private static final Font EMOJI_FONT = new Font("Segoe UI Emoji", Font.PLAIN, 18);

    private static final Color GUIDE_LINE_COLOR = new Color(226, 232, 240);
    private static final Color WAITING_TEXT_COLOR = new Color(148, 163, 184);
    private static final Color GLOW_COLOR = new Color(254, 202, 202, 160);
    private static final Color WORD_BOX_BG_COLOR = new Color(254, 226, 226);
    private static final Color WORD_BOX_BORDER_COLOR = new Color(220, 38, 38);
    private static final Color WORD_TEXT_COLOR = new Color(153, 27, 27);
    private static final Color EMOJI_TEXT_COLOR = new Color(30, 41, 59);

    private static final BasicStroke DASHED_GUIDE_STROKE = new BasicStroke(
            1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
            10.0f, new float[]{6.0f, 6.0f}, 0.0f);
    private static final BasicStroke DEFAULT_LINE_STROKE = new BasicStroke(1.5f);
    private static final BasicStroke WORD_BOX_STROKE = new BasicStroke(2f);

    private JPanel topPanel;
    private JLabel titleLabel;
    private AttackItemWord fallingItemWord;

    /** @wbp.parser.constructor */
    public ItemWordPanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setMinimumSize(new Dimension(MIN_PANEL_WIDTH, PANEL_HEIGHT));
        setSize(PANEL_WIDTH, PANEL_HEIGHT);
        setLayout(new BorderLayout(0, 0));
        setBackground(PANEL_BG_COLOR);
        setBorder(BorderFactory.createLineBorder(PANEL_BORDER_COLOR, 2));

        // 상단: 아이템 활성 상태 타이틀 영역
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 8, 10, 8));
        topPanel.setBackground(TOP_PANEL_BG_COLOR);

        titleLabel = new JLabel("아이템", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        updateHeaderVisual(false);
    }

    // 아이템 활성화 여부에 따라 상단 헤더 문구와 색상 변경
    private void updateHeaderVisual(boolean active) {
        if (titleLabel == null) {
            return;
        }

        if (active) {
            titleLabel.setText("아이템 활성!");
            titleLabel.setForeground(ACTIVE_TITLE_COLOR);
        } else {
            titleLabel.setText("아이템");
            titleLabel.setForeground(INACTIVE_TITLE_COLOR);
        }
    }

    // 표시 가능한 유효한 아이템 단어인지 검사
    private boolean hasValidItem(AttackItemWord item) {
        return item != null && item.getText() != null && !item.getText().isBlank();
    }

    // 아이템 타입 문자열에 대응하는 이모지 반환
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

    // 서버에서 받은 아이템 단어 상태를 반영하고 화면 갱신
    public void updateFallingItem(AttackItemWord item) {
        runOnEdt(() -> {
            this.fallingItemWord = item;
            updateHeaderVisual(hasValidItem(item));
            repaint();
        });
    }

    // 활성 아이템 단어를 제거하고 대기 상태로 전환
    public void clearFallingItem() {
        updateFallingItem(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int topOffset = (topPanel != null && topPanel.getHeight() > 0) ? topPanel.getHeight() : 42;
        int centerX = getWidth() / 2;

        // 중앙 점선 가이드 라인
        g2d.setColor(GUIDE_LINE_COLOR);
        g2d.setStroke(DASHED_GUIDE_STROKE);
        g2d.drawLine(centerX, topOffset + 10, centerX, getHeight() - 16);
        g2d.setStroke(DEFAULT_LINE_STROKE);

        // 활성 아이템 단어가 없으면 대기 문구만 표시
        if (!hasValidItem(fallingItemWord)) {
            g2d.setFont(WAITING_FONT);
            g2d.setColor(WAITING_TEXT_COLOR);
            String waitingText = "아이템 대기 중";
            FontMetrics fm = g2d.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(waitingText)) / 2;
            int ty = (getHeight() + topOffset) / 2;
            g2d.drawString(waitingText, tx, ty);
            g2d.dispose();
            return;
        }

        // 아이템 단어 박스 및 텍스트 렌더링 (Y 좌표는 서버 기준 높이를 패널 높이에 비례 변환)
        String text = fallingItemWord.getText();
        g2d.setFont(WORD_FONT);
        FontMetrics fm = g2d.getFontMetrics();

        int padX = 12;
        int padY = 6;
        int boxWidth = Math.min(getWidth() - 16, fm.stringWidth(text) + padX * 2);
        int boxHeight = fm.getHeight() + padY * 2;
        int boxX = (getWidth() - boxWidth) / 2;
        int boxY = (int) ((fallingItemWord.getY() / SERVER_SCREEN_HEIGHT) * getHeight());

        g2d.setColor(GLOW_COLOR);
        g2d.fillRoundRect(boxX - 3, boxY - 3, boxWidth + 6, boxHeight + 6, 14, 14);

        g2d.setColor(WORD_BOX_BG_COLOR);
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 12, 12);

        g2d.setColor(WORD_BOX_BORDER_COLOR);
        g2d.setStroke(WORD_BOX_STROKE);
        g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 12, 12);

        g2d.setColor(WORD_TEXT_COLOR);
        int textX = boxX + (boxWidth - fm.stringWidth(text)) / 2;
        int textY = boxY + ((boxHeight - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, textX, textY);

        // 단어 박스 상단 중앙에 아이템 이모지 표시
        String emoji = getItemEmoji(fallingItemWord.getItemtype());
        if (!emoji.isEmpty()) {
            g2d.setFont(EMOJI_FONT);
            g2d.setColor(EMOJI_TEXT_COLOR);
            FontMetrics emojiFm = g2d.getFontMetrics();
            int emojiX = boxX + (boxWidth - emojiFm.stringWidth(emoji)) / 2;
            int emojiY = boxY - 6;
            g2d.drawString(emoji, emojiX, emojiY);
        }

        g2d.dispose();
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