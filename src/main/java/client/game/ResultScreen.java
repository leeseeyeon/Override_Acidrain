package client.game;

import javax.swing.*;

public interface ResultScreen {
    void renderBannerAndTheme(JPanel panel);
    String getResultTitleText();
}