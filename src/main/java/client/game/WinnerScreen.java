package client.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class WinnerScreen implements ResultScreen {
    private Image victoryBannerImage;
    private Color backgroundColor;

    public WinnerScreen() {
        this.backgroundColor = Color.WHITE;
        this.victoryBannerImage = loadBannerImage("/images/winner_banner.png");
    }

    public WinnerScreen(Image victoryBannerImage, Color backgroundColor) {
        this.victoryBannerImage = victoryBannerImage;
        this.backgroundColor = (backgroundColor != null) ? backgroundColor : Color.WHITE;
    }

    private Image loadBannerImage(String resourcePath) {
        URL imageUrl = getClass().getResource(resourcePath);
        if (imageUrl == null) {
            return null;
        }
        return new ImageIcon(imageUrl).getImage();
    }

    @Override
    public void renderBannerAndTheme(JPanel panel) {
        if (panel == null) {
            return;
        }

        panel.removeAll();
        panel.setLayout(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setOpaque(true);
        panel.setBorder(new EmptyBorder(5, 20, 15, 20));

        if (victoryBannerImage != null) {
            Image scaledImage = victoryBannerImage.getScaledInstance(340, 110, Image.SCALE_SMOOTH);
            JLabel bannerImageLabel = new JLabel(new ImageIcon(scaledImage), SwingConstants.CENTER);
            panel.add(bannerImageLabel, BorderLayout.CENTER);
        } else {
            JLabel titleLabel = new JLabel(getResultTitleText(), SwingConstants.CENTER);
            titleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 72));
            titleLabel.setForeground(Color.BLACK);
            panel.add(titleLabel, BorderLayout.CENTER);
        }

        panel.revalidate();
        panel.repaint();
    }

    @Override
    public String getResultTitleText() {
        return "Winner";
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        if (backgroundColor != null) {
            this.backgroundColor = backgroundColor;
        }
    }

    public void setVictoryBannerImage(Image victoryBannerImage) {
        this.victoryBannerImage = victoryBannerImage;
    }
}