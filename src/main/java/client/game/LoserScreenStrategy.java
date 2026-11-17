package client.game;

import javax.swing.*;
import java.awt.*;

public class LoserScreenStrategy implements ResultScreenStrategy {
    private Image defeatBannerImage;
    private Color backgroundColor;

    @Override
    public void renderBannerAndTheme(JPanel panel) {
        // TODO: 패배 테마 배경 및 배너 렌더링
    }

    @Override
    public String getResultTitleText() {
        // TODO: 패배 타이틀 텍스트 반환 ("DEFEAT" 등)
        return "DEFEAT";
    }
}