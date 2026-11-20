package client.game;

import javax.swing.*;
import java.awt.*;

public class WinnerScreenStrategy implements ResultScreenStrategy {
    private Image victoryBannerImage;
    private Color backgroundColor;

    @Override
    public void renderBannerAndTheme(JPanel panel) {
        // TODO: 승리 테마 배경 및 배너 렌더링
    }

    @Override
    public String getResultTitleText() {
        // TODO: 승리 타이틀 텍스트 반환 ("VICTORY" 등)
        return "VICTORY";
    }
}