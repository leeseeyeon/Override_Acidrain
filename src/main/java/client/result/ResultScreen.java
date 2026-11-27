package client.result;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

// 승/패 결과 화면의 배너 및 테마 렌더링 공통 인터페이스
public interface ResultScreen {
    int BANNER_WIDTH = 340;
    int BANNER_HEIGHT = 110;
    Font TITLE_FONT = new Font("맑은 고딕", Font.PLAIN, 72);
    Border PANEL_BORDER = new EmptyBorder(5, 20, 15, 20);

    String getResultTitleText();
    Image getBannerImage();
    Color getBackgroundColor();

    // 배너 이미지 또는 기본 텍스트와 배경 테마를 패널에 공통 렌더링
    default void renderBannerAndTheme(JPanel panel) {
        if (panel == null) {
            return;
        }

        Color bgColor = (getBackgroundColor() != null) ? getBackgroundColor() : Color.WHITE;

        panel.removeAll();
        panel.setLayout(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setOpaque(true);
        panel.setBorder(PANEL_BORDER);

        Image bannerImage = getBannerImage();
        if (bannerImage != null) {
            Image scaledImage = bannerImage.getScaledInstance(BANNER_WIDTH, BANNER_HEIGHT, Image.SCALE_SMOOTH);
            panel.add(new JLabel(new ImageIcon(scaledImage), SwingConstants.CENTER), BorderLayout.CENTER);
        } else {
            JLabel titleLabel = new JLabel(getResultTitleText(), SwingConstants.CENTER);
            titleLabel.setFont(TITLE_FONT);
            titleLabel.setForeground(Color.BLACK);
            panel.add(titleLabel, BorderLayout.CENTER);
        }

        panel.revalidate();
        panel.repaint();
    }

    // 클래스패스 리소스에서 배너 이미지를 로드
    static Image loadBannerImage(String resourcePath) {
        URL imageUrl = ResultScreen.class.getResource(resourcePath);
        if (imageUrl == null) {
            return null;
        }
        return new ImageIcon(imageUrl).getImage();
    }
}