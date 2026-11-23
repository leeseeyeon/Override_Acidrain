package client.lobby;

import java.awt.*;

import javax.swing.*;

/*
플레이어 카드 (View 부품) - Player1(방장) / Player2(참가자) 두 번 생성해서 사용
 */
public class PlayerCardPanel extends JPanel {
    private static final String EMPTY_SLOT_TEXT = "입장 대기 중…";
    
    private static final Color READY_BADGE_COLOR = new Color(91, 91, 91); // 준비 완료!: 진한 회색
    private static final Color NOT_READY_BADGE_COLOR = new Color(193,193,193); // 준비 중...: 연한 회색

    private JLabel slotTitleLabel;
    private JLabel nicknameLabel;
    private JLabel statusLabel;

    /** WindowBuilder가 LobbyView 디자인 화면에서 이 패널을 그리려면 매개변수 없는 생성자가 필요 */
    public PlayerCardPanel() {
    	setBackground(new Color(255, 255, 255));
    	setBorder(BorderFactory.createLineBorder(new Color(0,0,0), 2));  
        setLayout(null);

        slotTitleLabel = new JLabel("Player");
        slotTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        slotTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        slotTitleLabel.setBounds(10, 30, 200, 36);
        add(slotTitleLabel);

        nicknameLabel = new JLabel(EMPTY_SLOT_TEXT);
        nicknameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nicknameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        nicknameLabel.setBounds(10, 170, 200, 44);
        add(nicknameLabel);

        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        statusLabel.setBounds(31, 289, 155, 78);
        add(statusLabel);
    }

    // ===== LobbyView가 쓰는 메소드 (유지) =====

    /** "Player1", "Player2" */
    public void setSlotTitle(String slotTitle) {
        slotTitleLabel.setText(slotTitle);
    }

    /** null이면 빈 자리로 표시 */
    public void setNickname(String nickname) {
        nicknameLabel.setText(nickname == null ? EMPTY_SLOT_TEXT : nickname);
    }

    /** "방장", "나"처럼 배경 없는 글자로 표시 (빈 문자열이면 아래 칸이 비어 보임) */
    public void showStatusText(String statusText) {
        statusLabel.setOpaque(false);
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setText(statusText);
        statusLabel.repaint();
    }

    /** 방장 화면에서 참가자 카드에 보이는 준비 상태 박스 */
    public void showReadyBadge(boolean isReady) {
        statusLabel.setOpaque(true);
        statusLabel.setBackground(isReady ? READY_BADGE_COLOR : NOT_READY_BADGE_COLOR); //게스트 준비 상태에 따라 방장 화면의 버튼 색 다르게
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setText(isReady ? "준비 완료!" : "준비 중…");
        statusLabel.repaint();
    }
}