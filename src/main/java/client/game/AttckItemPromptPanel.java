package client.game;

import javax.swing.*;
import java.awt.*;

public class AttackItemPromptPanel extends JPanel {
    private JLabel bulbIconLabel;
    private AttackItemWord fallingItemWord;

    public AttackItemPromptPanel() {
        // TODO: 아이콘 및 아이템 프롬프트 UI 초기화
    }

    public void updateFallingItem(AttackItemWord item) {
        // TODO: 떨어지는 아이템 정보 갱신 및 repaint
        this.fallingItemWord = item;
        repaint();
    }

    public void clearFallingItem() {
        // TODO: 아이템 정보 초기화
        this.fallingItemWord = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // TODO: 아이템 관련 그래픽 요소 렌더링
    }
}
