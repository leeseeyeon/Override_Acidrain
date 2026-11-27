package client.lobby.panel;

import java.awt.*;
import java.util.function.*;

import javax.swing.*;

import common.message.*;
import common.type.Theme;

public class OptionPanel extends JPanel {
    private static final Color SELECTED_CARD_COLOR = new Color(30, 41, 59);    
    private static final Color SELECTED_TEXT_COLOR = Color.WHITE;              
    private static final Color UNSELECTED_CARD_COLOR = new Color(235, 240, 248);   
    private static final Color UNSELECTED_TEXT_COLOR = new Color(149, 149, 149);   
    private static final Color PANEL_BORDER_COLOR = new Color(200, 208, 220); 
    private static final Color PANEL_BODY_COLOR = new Color(248, 250, 252);   
    private static final Color HEADER_COLOR = new Color(235, 240, 248);   
    private static final String HOST_GUIDE_TEXT = "플레이할 단어 테마를 골라 주세요!";
    private static final String GUEST_GUIDE_TEXT = "방장이 단어 테마를 선택하고 있습니다";

    private JLabel titleLabel;
    private JLabel guideLabel;
    private JToggleButton basicThemeCard;
    private JToggleButton csThemeCard;
    private JToggleButton englishThemeCard;

    private Consumer<Theme> themeSelectListener; 
    private Theme shownTheme = Theme.BASIC;
    private boolean isEditable = false;

    public OptionPanel() {
        setBackground(PANEL_BODY_COLOR);
        setBorder(BorderFactory.createLineBorder(PANEL_BORDER_COLOR, 1));
        setPreferredSize(new Dimension(440, 400));
        setLayout(null);

        titleLabel = new JLabel("테마 선택");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setOpaque(true);                 
        titleLabel.setBackground(HEADER_COLOR);
        titleLabel.setBounds(1, 1, 438, 58);       
        add(titleLabel);

        guideLabel = new JLabel(GUEST_GUIDE_TEXT);
        guideLabel.setHorizontalAlignment(SwingConstants.CENTER);
        guideLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        guideLabel.setForeground(new Color(100, 100, 100));
        guideLabel.setBounds(0, 70, 440, 22);
        add(guideLabel);

        //ThemeCard1 : 기본 단어 테마
        basicThemeCard = new JToggleButton(Theme.BASIC.getDisplayName());
        basicThemeCard.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        basicThemeCard.setBounds(50, 108, 340, 80);
        add(basicThemeCard);

        //ThemeCard2 : 컴퓨터 용어 테마
        csThemeCard = new JToggleButton(Theme.CS.getDisplayName());
        csThemeCard.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        csThemeCard.setBounds(50, 200, 340, 80);
        add(csThemeCard);

        //ThemeCard3 : 영단어 테마
        englishThemeCard = new JToggleButton(Theme.ENGLISH.getDisplayName());
        englishThemeCard.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        englishThemeCard.setBounds(50, 292, 340, 80);
        add(englishThemeCard);

        setUpThemeCards(); 
    }

    private JToggleButton[] allThemeCards() {
        return new JToggleButton[] { basicThemeCard, csThemeCard, englishThemeCard };
    }
    
    private void setUpThemeCards() {
        for (JToggleButton themeCard : allThemeCards()) {
            themeCard.setFocusPainted(false);
            themeCard.setContentAreaFilled(false); 
            themeCard.setOpaque(true);
            themeCard.addActionListener(e -> onThemeCardClicked(toTheme(themeCard)));
        }
        showSelectedTheme(Theme.BASIC); 
        setEditable(false); 
    }

    private Theme toTheme(JToggleButton themeCard) {
        if (themeCard == csThemeCard) return Theme.CS;
        if (themeCard == englishThemeCard) return Theme.ENGLISH;
        return Theme.BASIC;
    }

    private JToggleButton toThemeCard(Theme theme) {
        return switch (theme) {
            case CS      -> csThemeCard;
            case ENGLISH -> englishThemeCard;
            default      -> basicThemeCard;
        };
    }

    private void onThemeCardClicked(Theme clickedTheme) {
        showSelectedTheme(shownTheme); 
        if (!isEditable || themeSelectListener == null) {
            return;
        }
        themeSelectListener.accept(clickedTheme);
    }

// LobbyView 사용 메소드
    public void setThemeSelectListener(Consumer<Theme> themeSelectListener) {
        this.themeSelectListener = themeSelectListener;
    }

    public void showSelectedTheme(Theme selectedTheme) {
        shownTheme = selectedTheme;
        for (JToggleButton themeCard : allThemeCards()) {
            boolean isSelected = toTheme(themeCard) == selectedTheme;
            themeCard.setSelected(isSelected);
            themeCard.setBackground(isSelected ? SELECTED_CARD_COLOR : UNSELECTED_CARD_COLOR);
            themeCard.setForeground(isSelected ? SELECTED_TEXT_COLOR : UNSELECTED_TEXT_COLOR);
            themeCard.setBorder(BorderFactory.createLineBorder(isSelected ? SELECTED_CARD_COLOR : UNSELECTED_CARD_COLOR));
        }
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
        guideLabel.setText(isEditable ? HOST_GUIDE_TEXT : GUEST_GUIDE_TEXT);
        Cursor cursor = Cursor.getPredefinedCursor(isEditable ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR);
        for (JToggleButton themeCard : allThemeCards()) {
            themeCard.setCursor(cursor);
        }
    }
}