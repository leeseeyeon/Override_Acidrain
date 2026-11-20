package client.game;

import javax.swing.*;

public interface ResultScreenStrategy {
    void renderBannerAndTheme(JPanel panel);
    String getResultTitleText();
}