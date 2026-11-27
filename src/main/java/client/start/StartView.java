package client.start;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;

public class StartView extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);  
    private static final Color DESCRIPTION_COLOR = new Color(226, 232, 240);   
    private static final Color FORM_COLOR = new Color(235, 240, 248);           
    private static final Color FORM_BORDER_COLOR = new Color(169, 170, 171);   
    private static final Color BUTTON_COLOR = new Color(70, 99, 196);              
    private static final Color ERROR_TEXT_COLOR = new Color(230, 30, 30);         
    private static final Color PLACEHOLDER_COLOR = new Color(184, 184, 184);       
    private static final String PLACEHOLDER_TEXT = "사용할 닉네임을 입력하세요";
    private static final String LOGO_PATH = "/images/start_logo.png";  
    private static final int LOGO_HEIGHT = 340;                        

    private JLabel logoLabel;
    private JPanel descriptionPanel;
    private JLabel descriptionLabel;
    private JPanel nicknameFormPanel;
    private JLabel nicknameGuideLabel;
    private JTextField nicknameInputField;
    private JButton gameStartButton;
    private JLabel errorMessageLabel;

    public StartView() {
        setUpPanel();
        createDescription();
        createLogo();
        createNicknameForm();
    }

    private void setUpPanel() {
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(1000, 760));
        setLayout(null);
    }

    private void createDescription() {
        descriptionPanel = new JPanel();
        descriptionPanel.setBackground(DESCRIPTION_COLOR);
        descriptionPanel.setBounds(0, 388, 1000, 84); 
        descriptionPanel.setLayout(null);
        add(descriptionPanel);

        descriptionLabel = new JLabel("<html><center>단어로 된 산성비가 내리는 세상, <br>" + "살아남기 위해서는 누구보다 빠르고 정확한 타자 실력이 필요합니다.<br>"
                + "자신의 타자실력을 증명하고 승리를 차지하세요!</center></html>");
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descriptionLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        descriptionLabel.setBounds(0, 0, 1000, 84);  
        descriptionPanel.add(descriptionLabel);
    }

    private void createLogo() {
        logoLabel = new JLabel("SSS급 타자천재가 키보드를 잡았다"); 
        logoLabel.setBounds(0, 20, 1000, 350);
        add(logoLabel);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 60));
        showLogoImage();
    }

    private void createNicknameForm() {
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

        createNicknameInputField();   // 둥근 입력칸은 길어서 따로 분리

        errorMessageLabel = new JLabel(" ");
        errorMessageLabel.setForeground(ERROR_TEXT_COLOR);
        errorMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorMessageLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        errorMessageLabel.setBounds(27, 64, 368, 37);
        nicknameFormPanel.add(errorMessageLabel);

        gameStartButton = new JButton("게임 입장");
        gameStartButton.setForeground(Color.WHITE);
        gameStartButton.setBackground(BUTTON_COLOR);
        gameStartButton.setContentAreaFilled(false);  
        gameStartButton.setOpaque(true);              
        gameStartButton.setBorderPainted(false);
        gameStartButton.setFocusPainted(false);
        gameStartButton.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        gameStartButton.setBounds(111, 124, 200, 66);
        nicknameFormPanel.add(gameStartButton);
    }

    private void createNicknameInputField() {
        nicknameInputField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);  
                g2.dispose();

                super.paintComponent(g);

                if (getText().isEmpty()) {
                    g.setColor(PLACEHOLDER_COLOR);
                    g.setFont(getFont().deriveFont(13f));
                    g.drawString(PLACEHOLDER_TEXT, getInsets().left, getHeight() / 2 + 5);
                }
            }
        };
        nicknameInputField.setOpaque(false);  
        nicknameInputField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); 
        nicknameInputField.setToolTipText(PLACEHOLDER_TEXT);
        nicknameInputField.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        nicknameInputField.setBounds(108, 23, 265, 32);
        nicknameFormPanel.add(nicknameInputField);
    }

    private void showLogoImage() {
        URL logoUrl = getClass().getResource(LOGO_PATH);
        if (logoUrl == null) {  
            System.err.println("[StartView] 로고 이미지를 찾을 수 없음: " + LOGO_PATH);
            return;
        }
        ImageIcon originalLogo = new ImageIcon(logoUrl);
        int logoWidth = originalLogo.getIconWidth() * LOGO_HEIGHT / originalLogo.getIconHeight(); 
        Image scaledLogo = originalLogo.getImage().getScaledInstance(logoWidth, LOGO_HEIGHT, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledLogo));
        logoLabel.setText(null); 
    }

    //Controller 사용 메소드
    /** 입력한 닉네임 리턴 */
    public String getEnteredNickname() {
        return nicknameInputField.getText();
    }

    /** 에러메세지 받아와 세팅 */
    public void showErrorMessage(String message) {
        errorMessageLabel.setText(message);
    }

    public void setStartButtonListener(ActionListener listener) {
        gameStartButton.addActionListener(listener);
        nicknameInputField.addActionListener(listener);
    }
}