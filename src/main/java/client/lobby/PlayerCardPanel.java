package client.lobby;

import java.awt.*;

import javax.swing.*;

/*
플레이어 카드 (View 부품) - Player1(방장) / Player2(참가자) 두 번 생성해서 사용
 */
public class PlayerCardPanel extends JPanel {
    private static final String EMPTY_SLOT_TEXT = "입장 대기 중…";

    private static final Color CARD_BORDER_COLOR = new Color(200, 208, 220);      // 카드 테두리
    private static final Color CARD_BODY_COLOR = new Color(248, 250, 252);        // 카드 바탕
    private static final Color HEADER_COLOR = new Color(235, 240, 248);           // 제목 띠
    private static final Color STATUS_TEXT_COLOR = new Color(120, 120, 120);      

    private static final Color READY_BADGE_COLOR = new Color(46, 204, 113);       // 준비 완료!
    private static final Color READY_TEXT_COLOR = Color.WHITE;                  
    private static final Color NOT_READY_BADGE_COLOR = new Color(212, 212, 212);  // 준비 중…
    private static final Color NOT_READY_TEXT_COLOR = new Color(151, 151, 151);  

    private JLabel slotTitleLabel;
    private JLabel nicknameLabel;
    private JLabel statusLabel;

    /** WindowBuilder가 LobbyView 디자인 화면에서 이 패널을 그리려면 매개변수 없는 생성자가 필요 */
    public PlayerCardPanel() {
        setBackground(CARD_BODY_COLOR);
        setBorder(BorderFactory.createLineBorder(CARD_BORDER_COLOR, 1));
        setLayout(null);

        slotTitleLabel = new JLabel("Player");
        slotTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        slotTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        slotTitleLabel.setOpaque(true);                 // 배경색을 칠해서 제목 띠로 사용
        slotTitleLabel.setBackground(HEADER_COLOR);
        slotTitleLabel.setBounds(1, 1, 218, 58);        // 테두리(1) 안쪽을 가로로 꽉 채움
        add(slotTitleLabel);

        nicknameLabel = new JLabel(EMPTY_SLOT_TEXT);
        nicknameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nicknameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        nicknameLabel.setBounds(1, 170, 218, 90);       // 두 줄이 들어가는 높이
        add(nicknameLabel);

        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        statusLabel.setBounds(53, 300, 114, 60);        // 가운데 정렬: (220 - 114) / 2 = 53
        add(statusLabel);
    }

    // ===== LobbyView가 쓰는 메소드 (유지) =====

    /** 카드 맨 위 제목 띠의 글자 */
    public void setSlotTitle(String slotTitle) {
        slotTitleLabel.setText(slotTitle);
    }

    /** null이면 빈 자리로 표시. 닉네임이 6자보다 길면 5글자 뒤에서 줄을 바꿈 */
    public void setNickname(String nickname) {
        if (nickname == null) {
            nicknameLabel.setText(EMPTY_SLOT_TEXT);
        } else if (nickname.length() > 6) {
            nicknameLabel.setText("<html><center>" + nickname.substring(0, 5) + "<br>"
                    + nickname.substring(5) + "</center></html>");
        } else {
            nicknameLabel.setText(nickname);
        }
    }

    /** "방장", "나"처럼 배경 없는 글자로 표시 (빈 문자열이면 아래 칸이 비어 보임) */
    public void showStatusText(String statusText) {
        statusLabel.setOpaque(false);
        statusLabel.setForeground(STATUS_TEXT_COLOR);
        statusLabel.setText(statusText);
        statusLabel.repaint();
    }

    /** 방장 화면에서 참가자 카드에 보이는 준비 상태 박스 (준비 완료: 초록 / 준비 중: 연한 회색) */
    public void showReadyBadge(boolean isReady) {
        statusLabel.setOpaque(true);
        statusLabel.setBackground(isReady ? READY_BADGE_COLOR : NOT_READY_BADGE_COLOR);
        statusLabel.setForeground(isReady ? READY_TEXT_COLOR : NOT_READY_TEXT_COLOR);
        statusLabel.setText(isReady ? "준비 완료!" : "준비 중…");
        statusLabel.repaint();
    }
}