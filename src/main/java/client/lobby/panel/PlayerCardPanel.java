package client.lobby.panel;

import java.awt.*;

import javax.swing.*;

public class PlayerCardPanel extends JPanel {
    private static final String EMPTY_SLOT_TEXT = "입장 대기 중…";

    private static final Color CARD_BORDER_COLOR = new Color(200, 208, 220);     
    private static final Color CARD_BODY_COLOR = new Color(248, 250, 252);       
    private static final Color HEADER_COLOR = new Color(235, 240, 248);         
    private static final Color STATUS_TEXT_COLOR = new Color(120, 120, 120);      

    private static final Color READY_BADGE_COLOR = new Color(46, 204, 113);       // 준비 완료!
    private static final Color READY_TEXT_COLOR = Color.WHITE;                  
    private static final Color NOT_READY_BADGE_COLOR = new Color(212, 212, 212);  // 준비 중…
    private static final Color NOT_READY_TEXT_COLOR = new Color(151, 151, 151);  

    private JLabel slotTitleLabel;
    private JLabel nicknameLabel;
    private JLabel statusLabel;

    public PlayerCardPanel() {
        setUpPanel();
        createSlotTitle();
        createNicknameLabel();
        createStatusLabel();
    }

    private void setUpPanel() {
        setBackground(CARD_BODY_COLOR);
        setBorder(BorderFactory.createLineBorder(CARD_BORDER_COLOR, 1));
        setLayout(null);
    }

    private void createSlotTitle() {
        slotTitleLabel = new JLabel("Player");
        slotTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        slotTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        slotTitleLabel.setOpaque(true);              
        slotTitleLabel.setBackground(HEADER_COLOR);
        slotTitleLabel.setBounds(1, 1, 218, 58);      
        add(slotTitleLabel);
    }

    private void createNicknameLabel() {
        nicknameLabel = new JLabel(EMPTY_SLOT_TEXT);
        nicknameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nicknameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        nicknameLabel.setBounds(1, 170, 218, 90);      
        add(nicknameLabel);
    }

    private void createStatusLabel() {
        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        statusLabel.setBounds(53, 300, 114, 60);  
        add(statusLabel);
    }

//LobbyView 사용 메소드

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

    public void showStatusText(String statusText) {
        statusLabel.setOpaque(false);
        statusLabel.setForeground(STATUS_TEXT_COLOR);
        statusLabel.setText(statusText);
        statusLabel.repaint();
    }

    /** 방장 화면에서 게스트 카드에 보이는 준비 상태 박스*/
    public void showReadyBadge(boolean isReady) {
        statusLabel.setOpaque(true);
        statusLabel.setBackground(isReady ? READY_BADGE_COLOR : NOT_READY_BADGE_COLOR);
        statusLabel.setForeground(isReady ? READY_TEXT_COLOR : NOT_READY_TEXT_COLOR);
        statusLabel.setText(isReady ? "준비 완료!" : "준비 중…");
        statusLabel.repaint();
    }
}