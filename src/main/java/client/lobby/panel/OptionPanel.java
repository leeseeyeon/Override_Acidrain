package client.lobby.panel;

import java.awt.*;
import java.util.*;
import java.util.function.*;

import javax.swing.*;

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

    private static final int CARD_X = 49;
    private static final int CARD_WIDTH = 340;
    private static final int CARD_HEIGHT = 80;
    private static final int CARD_GAP = 12;         
    private static final int LIST_MARGIN = 8;        

    private JLabel titleLabel;
    private JLabel guideLabel;
    private JScrollPane themeScrollPane;  
    private JPanel themeListPanel;       

    private final Map<Theme, JToggleButton> themeCards = new EnumMap<>(Theme.class);

    private Consumer<Theme> themeSelectListener; 
    private Theme shownTheme = Theme.BASIC;
    private boolean isEditable = false;

    public OptionPanel() {
        setUpPanel();
        createHeader();
        createThemeList();
        createThemeCards();
        showSelectedTheme(Theme.BASIC); 
        setEditable(false); 
    }

    private void setUpPanel() {
        setBackground(PANEL_BODY_COLOR);
        setBorder(BorderFactory.createLineBorder(PANEL_BORDER_COLOR, 1));
        setPreferredSize(new Dimension(440, 400));
        setLayout(null);
    }

    private void createHeader() {
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
    }

    private void createThemeList() {
        themeListPanel = new JPanel();
        themeListPanel.setBackground(PANEL_BODY_COLOR);
        themeListPanel.setLayout(null);

        themeScrollPane = new JScrollPane(themeListPanel);
        themeScrollPane.setBorder(BorderFactory.createEmptyBorder());
        themeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
        themeScrollPane.getVerticalScrollBar().setUnitIncrement(16);   
        themeScrollPane.setBounds(1, 100, 438, 298);  
        add(themeScrollPane);
    }

    private void createThemeCards() {
        int y = LIST_MARGIN;
        for (Theme theme : Theme.values()) {
            JToggleButton themeCard = new JToggleButton(theme.getDisplayName());
            themeCard.setFont(new Font("맑은 고딕", Font.BOLD, 24));
            themeCard.setBounds(CARD_X, y, CARD_WIDTH, CARD_HEIGHT);
            themeCard.setFocusPainted(false);
            themeCard.setContentAreaFilled(false); 
            themeCard.setOpaque(true);
            themeCard.addActionListener(e -> onThemeCardClicked(theme));  
            themeListPanel.add(themeCard);
            themeCards.put(theme, themeCard);
            y += CARD_HEIGHT + CARD_GAP;
        }
        int listHeight = y - CARD_GAP + LIST_MARGIN;   
        themeListPanel.setPreferredSize(new Dimension(CARD_X * 2 + CARD_WIDTH, listHeight));
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
        for (Map.Entry<Theme, JToggleButton> entry : themeCards.entrySet()) {
            boolean isSelected = entry.getKey() == selectedTheme;
            JToggleButton themeCard = entry.getValue();
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
        for (JToggleButton themeCard : themeCards.values()) {
            themeCard.setCursor(cursor);
        }
    }
}