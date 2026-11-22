package client.start;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * 시작 화면 (View)
 * 판단은 하지 않고 보여 주기만 함. 버튼이 눌렸을 때 할 일은 StartController가 정함.
 */
public class StartView extends JPanel {
    private JLabel logoLabel;
    private JPanel descriptionPanel;
    private JLabel descriptionLabel;
    private JPanel nicknameFormPanel;
    private JLabel nicknameGuideLabel;
    private JTextField nicknameInputField;
    private JButton gameStartButton;
    private JLabel errorMessageLabel;

    public StartView() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1000, 760));
        setLayout(null);

        descriptionPanel = new JPanel();
        descriptionPanel.setBackground(new Color(235, 235, 235));
        descriptionPanel.setBounds(0, 275, 1000, 160);
        descriptionPanel.setLayout(null);
        add(descriptionPanel);

        logoLabel = new JLabel("산성비");
        logoLabel.setBounds(0, 173, 1000, 90);
        add(logoLabel);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 56));
        
        descriptionLabel = new JLabel("<시나리오 설명>");   // TODO: 실제 설명 문구! 수정 필요.
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descriptionLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        descriptionLabel.setBounds(0, 60, 1000, 40);
        descriptionPanel.add(descriptionLabel);

        nicknameFormPanel = new JPanel();
        nicknameFormPanel.setBackground(new Color(217, 217, 217));
        nicknameFormPanel.setBounds(286, 496, 420, 200);
        nicknameFormPanel.setLayout(null);
        add(nicknameFormPanel);

        nicknameGuideLabel = new JLabel("닉네임 :");
        nicknameGuideLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        nicknameGuideLabel.setBounds(35, 22, 61, 32);
        nicknameFormPanel.add(nicknameGuideLabel);

        nicknameInputField = new JTextField();
        nicknameInputField.setToolTipText("사용할 닉네임을 입력하세요");
        nicknameInputField.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        nicknameInputField.setBounds(108, 23, 265, 32);
        nicknameFormPanel.add(nicknameInputField);

        errorMessageLabel = new JLabel(" ");
        errorMessageLabel.setForeground(new Color(192, 57, 43));
        errorMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorMessageLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        errorMessageLabel.setBounds(27, 64, 368, 37);
        nicknameFormPanel.add(errorMessageLabel);
        
        gameStartButton = new JButton("게임 입장");
        gameStartButton.setForeground(Color.WHITE);
        gameStartButton.setBackground(new Color(91, 91, 91));
        gameStartButton.setContentAreaFilled(false);   // 기본 버튼 모양 대신 직접 칠한 배경 사용 (윈도우 테마에서도 진한 색이 보이도록)
        gameStartButton.setOpaque(true);                // setContentAreaFilled 다음에 와야 함
        gameStartButton.setBorderPainted(false);
        gameStartButton.setFocusPainted(false);
        gameStartButton.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        gameStartButton.setBounds(111, 124, 200, 50);
        nicknameFormPanel.add(gameStartButton);


    }

    // ===== Controller가 쓰는 메소드 (유지) =====

    /** 입력한 그대로 돌려줌 (띄어쓰기도 검사해야 해서 앞뒤 공백을 지우지 않음) */
    public String getEnteredNickname() {
        return nicknameInputField.getText();
    }

    /** 버튼 아래 빨간 글자로 경고 표시. " "를 넘기면 지워진 것처럼 보임 */
    public void showErrorMessage(String message) {
        errorMessageLabel.setText(message);
    }

    /** 버튼 클릭과 입력칸 Enter 모두 같은 동작으로 연결 */
    public void setStartButtonListener(ActionListener listener) {
        gameStartButton.addActionListener(listener);
        nicknameInputField.addActionListener(listener);
    }
}