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

public class LoserScreen implements ResultScreen {
    private Image defeatBannerImage;
    private Color backgroundColor;

    public LoserScreen() {
        this.backgroundColor = Color.WHITE;
        this.defeatBannerImage = loadBannerImage("/images/loser_banner.png");
    }

    public LoserScreen(Image defeatBannerImage, Color backgroundColor) {
        this.defeatBannerImage = defeatBannerImage;
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

        if (defeatBannerImage != null) {
            Image scaledImage = defeatBannerImage.getScaledInstance(340, 110, Image.SCALE_SMOOTH);
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
        return "Loser";
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        if (backgroundColor != null) {
            this.backgroundColor = backgroundColor;
        }
    }

    public void setDefeatBannerImage(Image defeatBannerImage) {
        this.defeatBannerImage = defeatBannerImage;
    }
}