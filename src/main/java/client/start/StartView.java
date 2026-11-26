package client.start;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;

/**
 * 시작 화면 (View)
 * 판단은 하지 않고 보여 주기만 함. 버튼이 눌렸을 때 할 일은 StartController가 정함.
 */
public class StartView extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);        // 화면 배경: 거의 흰색
    private static final Color DESCRIPTION_COLOR = new Color(226, 232, 240);       // 설명 띠: 연한 회청색 (대기실 배경과 같음)
    private static final Color FORM_COLOR = new Color(235, 240, 248);              // 닉네임 입력 상자: 연한 파랑
    private static final Color FORM_BORDER_COLOR = new Color(169, 170, 171);       // 입력 상자 테두리: 회색
    private static final Color BUTTON_COLOR = new Color(70, 99, 196);              // 버튼: 파랑 (대기실 버튼과 같음)
    private static final Color ERROR_TEXT_COLOR = new Color(230, 30, 30);          // 에러 글자: 빨강
    private static final Color PLACEHOLDER_COLOR = new Color(184, 184, 184);       // 입력칸 안내 글자: 연한 회색
    private static final String PLACEHOLDER_TEXT = "사용할 닉네임을 입력하세요";
    private static final String LOGO_PATH = "/images/start_logo.png";   // src/main/resources 기준 경로
    private static final int LOGO_HEIGHT = 340;                          // 화면에 보일 로고 높이 (너비는 비율대로)

    private JLabel logoLabel;
    private JPanel descriptionPanel;
    private JLabel descriptionLabel;
    private JPanel nicknameFormPanel;
    private JLabel nicknameGuideLabel;
    private JTextField nicknameInputField;
    private JButton gameStartButton;
    private JLabel errorMessageLabel;

    public StartView() {
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(1000, 760));
        setLayout(null);

        descriptionPanel = new JPanel();
        descriptionPanel.setBackground(DESCRIPTION_COLOR);
        descriptionPanel.setBounds(0, 388, 1000, 84);   // 얇은 띠
        descriptionPanel.setLayout(null);
        add(descriptionPanel);

        logoLabel = new JLabel("SSS급 타자천재가 키보드를 잡았다");   // 로고 이미지를 못 찾으면 이 글자가 대신 보임
        logoLabel.setBounds(0, 20, 1000, 350);
        add(logoLabel);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 60));
        showLogoImage();
        
        descriptionLabel = new JLabel("<html><center>단어로 된 산성비가 내리는 세상, <br>" + "살아남기 위해서는 누구보다 빠르고 정확한 타자 실력이 필요합니다.<br>"
                + "자신의 타자실력을 증명하고 승리를 차지하세요!</center></html>"); // TODO: 실제 설명 문구! 수정 필요.
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descriptionLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        descriptionLabel.setBounds(0, 0, 1000, 84);   // 띠 높이와 같게 → 세로 가운데
        descriptionPanel.add(descriptionLabel);

        nicknameFormPanel = new JPanel();
        nicknameFormPanel.setBackground(FORM_COLOR);
        nicknameFormPanel.setBorder(BorderFactory.createLineBorder(FORM_BORDER_COLOR, 1));
        nicknameFormPanel.setBounds(290, 500, 420, 215);
        nicknameFormPanel.setLayout(null);
        add(nicknameFormPanel);

        nicknameGuideLabel = new JLabel("닉네임 :");
        nicknameGuideLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        nicknameGuideLabel.setBounds(35, 22, 61, 32);
        nicknameFormPanel.add(nicknameGuideLabel);

        nicknameInputField = new JTextField() {
            /* 모서리가 둥근 흰 바탕 + 비어 있을 때 회색 안내 글자 (그리기만 바꿈. 입력값 getText()에는 영향 없음) */
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);   // 둥근 바탕 (14 = 모서리 둥글기)
                g2.dispose();

                super.paintComponent(g);   // 입력한 글자와 커서

                if (getText().isEmpty()) {
                    g.setColor(PLACEHOLDER_COLOR);
                    g.setFont(getFont().deriveFont(13f));
                    g.drawString(PLACEHOLDER_TEXT, getInsets().left, getHeight() / 2 + 5);
                }
            }
        };
        nicknameInputField.setOpaque(false);   // 기본 네모 바탕 대신 위에서 둥근 바탕을 직접 칠함
        nicknameInputField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));   // 테두리 없이 좌우 여백만
        nicknameInputField.setToolTipText(PLACEHOLDER_TEXT);
        nicknameInputField.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        nicknameInputField.setBounds(108, 23, 265, 32);
        nicknameFormPanel.add(nicknameInputField);

        errorMessageLabel = new JLabel(" ");
        errorMessageLabel.setForeground(ERROR_TEXT_COLOR);
        errorMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorMessageLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        errorMessageLabel.setBounds(27, 64, 368, 37);
        nicknameFormPanel.add(errorMessageLabel);
        
        gameStartButton = new JButton("게임 입장");
        gameStartButton.setForeground(Color.WHITE);
        gameStartButton.setBackground(BUTTON_COLOR);
        gameStartButton.setContentAreaFilled(false);   // 기본 버튼 모양 대신 직접 칠한 배경 사용 (윈도우 테마에서도 진한 색이 보이도록)
        gameStartButton.setOpaque(true);                // setContentAreaFilled 다음에 와야 함
        gameStartButton.setBorderPainted(false);
        gameStartButton.setFocusPainted(false);
        gameStartButton.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        gameStartButton.setBounds(111, 124, 200, 66);
        nicknameFormPanel.add(gameStartButton);


    }

    /** src/main/resources/images/start_logo.png 를 높이 LOGO_HEIGHT에 맞춰 줄여서 로고 자리에 표시 */
    private void showLogoImage() {
        URL logoUrl = getClass().getResource(LOGO_PATH);
        if (logoUrl == null) {   // 파일이 없거나 경로가 틀림 → 글자 로고 그대로 둠
            System.err.println("[StartView] 로고 이미지를 찾을 수 없음: " + LOGO_PATH);
            return;
        }
        ImageIcon originalLogo = new ImageIcon(logoUrl);
        int logoWidth = originalLogo.getIconWidth() * LOGO_HEIGHT / originalLogo.getIconHeight();   // 가로세로 비율 유지
        Image scaledLogo = originalLogo.getImage().getScaledInstance(logoWidth, LOGO_HEIGHT, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledLogo));
        logoLabel.setText(null);   // 이미지만 보이게
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