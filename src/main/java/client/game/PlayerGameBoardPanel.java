package client.game;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PlayerGameBoardPanel extends JPanel {
    private JLabel playerNameLabel;
    private JProgressBar hpGaugeBar;
    private List<ClientFallingWord> fallingWords;
    private JTextField wordInputField;
    private boolean isShadowEffectActive;

    public PlayerGameBoardPanel() {
        // TODO: 레이아웃 초기화 및 컴포넌트(라벨, 프로그레스바, 텍스트필드 등) 배치
    }

    public void updatePlayerStatus(PlayerStateData state) {
        // TODO: 플레이어 체력(hpGaugeBar) 및 닉네임 상태 갱신
    }

    public void updateFallingWords(List<ClientFallingWord> words) {
        // TODO: 떨어지는 단어 목록(fallingWords) 업데이트 및 repaint 요청
        this.fallingWords = words;
        repaint();
    }

    public String getEnteredWord() {
        // TODO: 텍스트필드에 입력된 단어 반환 후 공백 처리
        return null;
    }

    public void clearInputField() {
        // TODO: 단어 입력창 텍스트 비우기
    }

    public void setInputEditable(boolean editable) {
        // TODO: 입력창 활성화/비활성화 설정
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // TODO: 떨어지는 단어들을 화면에 렌더링하는 로직 구현
    }
}