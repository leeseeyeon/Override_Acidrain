package client.result;

import java.awt.Color;
import java.awt.Image;

// 게임 패배 결과 화면 전략 클래스
public class LoserScreen implements ResultScreen {
    private static final String DEFAULT_BANNER_PATH = "/images/loser_banner.png";

    private Image defeatBannerImage;
    private Color backgroundColor;

    public LoserScreen() {
        this(ResultScreen.loadBannerImage(DEFAULT_BANNER_PATH), Color.WHITE);
    }

    public LoserScreen(Image defeatBannerImage, Color backgroundColor) {
        this.defeatBannerImage = defeatBannerImage;
        this.backgroundColor = (backgroundColor != null) ? backgroundColor : Color.WHITE;
    }

    @Override
    public String getResultTitleText() {
        return "Loser";
    }

    @Override
    public Image getBannerImage() {
        return defeatBannerImage;
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

    public void setDefeatBannerImage(Image defeatBannerImage) {
        this.defeatBannerImage = defeatBannerImage;
    }
}