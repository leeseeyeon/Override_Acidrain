package client.result;

import java.awt.Color;
import java.awt.Image;

// 게임 승리 결과 화면 전략 클래스
public class WinnerScreen implements ResultScreen {
    private static final String DEFAULT_BANNER_PATH = "/images/winner_banner.png";

    private Image victoryBannerImage;
    private Color backgroundColor;

    public WinnerScreen() {
        this(ResultScreen.loadBannerImage(DEFAULT_BANNER_PATH), Color.WHITE);
    }

    public WinnerScreen(Image victoryBannerImage, Color backgroundColor) {
        this.victoryBannerImage = victoryBannerImage;
        this.backgroundColor = (backgroundColor != null) ? backgroundColor : Color.WHITE;
    }

    @Override
    public String getResultTitleText() {
        return "Winner";
    }

    @Override
    public Image getBannerImage() {
        return victoryBannerImage;
    }

    @Override
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