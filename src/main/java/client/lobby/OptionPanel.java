package client.lobby;

import java.awt.*;
import java.util.function.*;

import javax.swing.*;

import common.message.*;

/*
옵션선택 영역 (View 부품) - 단어 테마 선택
테마마다 선택 카드(JToggleButton) 하나. 
방장만 고를 수 있고, 참가자는 방장이 고른 테마를 보기만 함
 */
public class OptionPanel extends JPanel {
    private static final Color SELECTED_CARD_COLOR = new Color(91, 91, 91);
    private static final Color UNSELECTED_CARD_COLOR = new Color(255, 255, 255);
    private static final String HOST_GUIDE_TEXT = "플레이할 단어 테마를 골라 주세요!";
    private static final String GUEST_GUIDE_TEXT = "방장이 단어 테마를 선택하고 있습니다";

    private JLabel titleLabel;
    private JLabel guideLabel;
    private JToggleButton basicThemeCard;
    private JToggleButton csThemeCard;
    private JToggleButton englishThemeCard;

    private Consumer<Theme> themeSelectListener; //선택된 카드
    private Theme shownTheme = Theme.BASIC;
    private boolean isEditable = false; //참가자인 경우 테마 선택 불가.

    public OptionPanel() {
        setBackground(new Color(193, 193, 193));
        setPreferredSize(new Dimension(440, 400));
        setLayout(null);

        titleLabel = new JLabel("옵션선택");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        titleLabel.setBounds(0, 26, 440, 36);
        add(titleLabel);

        guideLabel = new JLabel(GUEST_GUIDE_TEXT);
        guideLabel.setHorizontalAlignment(SwingConstants.CENTER);
        guideLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        guideLabel.setBounds(0, 68, 440, 22);
        add(guideLabel);

        //ThemeCard1 : 기본 단어 테마.
        basicThemeCard = new JToggleButton(Theme.BASIC.getDisplayName());
        basicThemeCard.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        basicThemeCard.setBounds(50, 108, 340, 80);
        add(basicThemeCard);

        //ThemeCard2 : 컴퓨터 용어 테마.
        csThemeCard = new JToggleButton(Theme.CS.getDisplayName());
        csThemeCard.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        csThemeCard.setBounds(50, 200, 340, 80);
        add(csThemeCard);

        //ThemeCard3 : 영단어 테마.
        englishThemeCard = new JToggleButton(Theme.ENGLISH.getDisplayName());
        englishThemeCard.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        englishThemeCard.setBounds(50, 292, 340, 80);
        add(englishThemeCard);

        setUpThemeCards(); //setUpThemeCard 메소드 실행
    }

    //모든 ThemeCard 버튼 객체를 배열에 저장해두자 (반복문에서 사용)
    private JToggleButton[] allThemeCards() {
        return new JToggleButton[] { basicThemeCard, csThemeCard, englishThemeCard };
    }
    
    //생성자 마지막 줄에서 실행. 카드 초기설정
    private void setUpThemeCards() {
        for (JToggleButton themeCard : allThemeCards()) {
            themeCard.setFocusPainted(false);
            themeCard.setContentAreaFilled(false);   // 기본 버튼 모양 대신 직접 칠한 배경을 사용
            themeCard.setOpaque(true);
            themeCard.addActionListener(e -> onThemeCardClicked(toTheme(themeCard)));
        }
        showSelectedTheme(Theme.BASIC); //처음에는 기본 테마 선택된 모습
        setEditable(false); //처음에는 guest 처럼 잠그고, 방장이면 controller 가 풀어줌.
    }

    //카드 -> 테마. 선택된 카드가 어떤 테마인지 알려줌.
    private Theme toTheme(JToggleButton themeCard) {
        if (themeCard == csThemeCard) return Theme.CS;
        if (themeCard == englishThemeCard) return Theme.ENGLISH;
        return Theme.BASIC;
    }
    //테마 -> 카드. 해당 테마가 어떤 카드에서 표시되는지.
    private JToggleButton toThemeCard(Theme theme) {
        return switch (theme) {
            case CS      -> csThemeCard;
            case ENGLISH -> englishThemeCard;
            default      -> basicThemeCard;
        };
    }

    private void onThemeCardClicked(Theme clickedTheme) {
        showSelectedTheme(shownTheme);   // 클릭만으로는 바뀌지 않음. 서버가 THEME를 돌려주면 그때 바뀜
        if (!isEditable || themeSelectListener == null) {
            return;
        }
        themeSelectListener.accept(clickedTheme);
    }

// ===== LobbyView가 쓰는 메소드 (유지) =====
    //선택된 테마를 알림.
    public void setThemeSelectListener(Consumer<Theme> themeSelectListener) {
        this.themeSelectListener = themeSelectListener;
    }

    //특정 ThemeCard가 선택될 경우 디자인 변경사항.
    public void showSelectedTheme(Theme selectedTheme) {
        shownTheme = selectedTheme;
        for (JToggleButton themeCard : allThemeCards()) {
            boolean isSelected = toTheme(themeCard) == selectedTheme;
            themeCard.setSelected(isSelected);
            themeCard.setBackground(isSelected ? SELECTED_CARD_COLOR : UNSELECTED_CARD_COLOR);
            themeCard.setForeground(isSelected ? Color.WHITE : Color.DARK_GRAY);
            themeCard.setBorder(BorderFactory.createLineBorder(isSelected ? SELECTED_CARD_COLOR : new Color(200, 190, 150)));
        }
    }

    //참가자는 setEnabled(false) 대신 클릭만 무시함
    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
        guideLabel.setText(isEditable ? HOST_GUIDE_TEXT : GUEST_GUIDE_TEXT);
        Cursor cursor = Cursor.getPredefinedCursor(isEditable ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR);
        for (JToggleButton themeCard : allThemeCards()) {
            themeCard.setCursor(cursor);
        }
    }
}