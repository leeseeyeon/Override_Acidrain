package client.game;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class AttackItemPromptPanel extends JPanel {
    private static final int PANEL_WIDTH = 180;
    private static final int PANEL_HEIGHT = 400;

    private JLabel bulbIconLabel;
    private AttackItemWord fallingItemWord;

    private ImageIcon activeBulbIcon;
    private ImageIcon inactiveBulbIcon;

    /** @wbp.parser.constructor */
    public AttackItemPromptPanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setMinimumSize(new Dimension(150, PANEL_HEIGHT));
        setSize(PANEL_WIDTH, PANEL_HEIGHT);
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createLineBorder(new Color(200, 208, 220), 2));

        // 전구 이미지 리소스 로드 (이미지가 없으면 텍스트 배지로 대체)
        activeBulbIcon = loadScaledIcon("/images/bulb_on.png", 28, 28);
        inactiveBulbIcon = loadScaledIcon("/images/bulb_off.png", 28, 28);

        // 상단: 전구 아이콘 및 아이템 타이틀 영역
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 8, 10, 8));
        topPanel.setBackground(new Color(235, 240, 248));

        bulbIconLabel = new JLabel("공격 아이템", SwingConstants.CENTER);
        bulbIconLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        bulbIconLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        bulbIconLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        bulbIconLabel.setIconTextGap(4);
        topPanel.add(bulbIconLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        updateBulbVisual(false);
    }

    private ImageIcon loadScaledIcon(String resourcePath, int width, int height) {
        URL imageUrl = getClass().getResource(resourcePath);
        if (imageUrl == null) {
            return null;
        }
        Image scaled = new ImageIcon(imageUrl).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private void updateBulbVisual(boolean active) {
        if (bulbIconLabel == null) {
            return;
        }

        if (active) {
            bulbIconLabel.setIcon(activeBulbIcon);
            bulbIconLabel.setText("공격 아이템 활성!");
            bulbIconLabel.setForeground(new Color(185, 28, 28));
        } else {
            bulbIconLabel.setIcon(inactiveBulbIcon);
            bulbIconLabel.setText("공격 아이템");
            bulbIconLabel.setForeground(new Color(71, 85, 105));
        }
    }

    public void updateFallingItem(AttackItemWord item) {
        Runnable updateTask = () -> {
            this.fallingItemWord = item;
            boolean hasItem = (item != null && item.getText() != null && !item.getText().isBlank());
            updateBulbVisual(hasItem);
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
            updateBulbVisual(false);
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

        int topOffset = 58; // 상단 전구 라벨 영역 아래부터 렌더링
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

        // 좁은 중앙 패널에서 잘리지 않도록 X는 가로 중앙 정렬, Y는 낙하 좌표 반영
        int boxX = (getWidth() - boxWidth) / 2;
        int rawY = (int) fallingItemWord.getY();
        int minY = topOffset + 10;
        int maxY = Math.max(minY, getHeight() - boxHeight - 10);
        int boxY = (rawY > 0) ? Math.max(minY, Math.min(rawY, maxY)) : (getHeight() + topOffset - boxHeight) / 2;

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

        g2d.dispose();
    }
}