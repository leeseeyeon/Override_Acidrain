package client.lobby;

import java.awt.*;
import java.awt.event.*;
import java.util.List;   // java.awt.List와 이름이 같아서 직접 지정
import java.util.function.*;

import javax.swing.*;

import common.message.*;

//MVC 중 View 담당. Observer!
public class LobbyView extends JPanel implements LobbyModelListener {
    private JLabel titleLabel;
    private PlayerCardPanel hostCardPanel;
    private OptionPanel optionPanel;
    private PlayerCardPanel guestCardPanel;
    private ChatPanel chatPanel;
    private JButton lobbyActionButton;

    /* 채팅창에 이미 표시한 메시지 수 (새로 생긴 것만 추가하기 위해) */
    private int shownChatMessageCount = 0;

    public LobbyView() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1000, 760));
        setLayout(null);

        titleLabel = new JLabel("대기실");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        titleLabel.setBounds(0, 15, 1000, 40);
        add(titleLabel);

        hostCardPanel = new PlayerCardPanel();
        hostCardPanel.setSlotTitle("Player1");
        hostCardPanel.setBounds(40, 70, 220, 400);
        add(hostCardPanel);

        optionPanel = new OptionPanel();
        optionPanel.setBounds(280, 70, 440, 400);
        add(optionPanel);

        guestCardPanel = new PlayerCardPanel();
        guestCardPanel.setSlotTitle("Player2");
        guestCardPanel.setBounds(740, 70, 220, 400);
        add(guestCardPanel);

        chatPanel = new ChatPanel();
        chatPanel.setBounds(40, 488, 920, 170);
        add(chatPanel);

        lobbyActionButton = new JButton("준비");   // 디자인 확인 용 기본 글자. 실행 중에는 Controller가 updateActionButton()으로 바꿈
        lobbyActionButton.setForeground(Color.WHITE);
        lobbyActionButton.setBackground(new Color(91, 91, 91));
        lobbyActionButton.setContentAreaFilled(false);   // 기본 버튼 모양 대신 직접 칠한 배경 사용 (윈도우 테마에서도 진한 색이 보이도록)
        lobbyActionButton.setOpaque(true);                // setContentAreaFilled 다음에 와야 함
        lobbyActionButton.setBorderPainted(false);
        lobbyActionButton.setFocusPainted(false);
        lobbyActionButton.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        lobbyActionButton.setBounds(340, 680, 320, 56);
        add(lobbyActionButton);
    }

// ====== Model (--> View 옵저) 로부터 알림 =====

    @Override
    public void onLobbyChanged(LobbyModel model) {
        showHostCard(model);
        showGuestCard(model);
        optionPanel.showSelectedTheme(model.getSelectedTheme());
        showNewChatMessages(model.getChatMessages());
    }

    private void showHostCard(LobbyModel model) {
        hostCardPanel.setNickname(model.getHostNickname());
        hostCardPanel.showStatusText(model.getHostNickname() == null ? "" : "방장");
    }

    private void showGuestCard(LobbyModel model) {
        guestCardPanel.setNickname(model.getGuestNickname());
        if (model.getGuestNickname() == null) {
            guestCardPanel.showStatusText("");
        } else if (!model.isLocalPlayerHost()) {
            guestCardPanel.showStatusText("나");
        } else {
            guestCardPanel.showReadyBadge(model.isGuestReady());
        }
    }

    private void showNewChatMessages(List<ChatMessage> chatMessages) {
        if (chatMessages.size() < shownChatMessageCount) {   // 방에 새로 들어와서 기록이 비워진 경우
            chatPanel.clearChatMessages();
            shownChatMessageCount = 0;
        }
        for (int i = shownChatMessageCount; i < chatMessages.size(); i++) {
            chatPanel.appendChatMessage(chatMessages.get(i));
        }
        shownChatMessageCount = chatMessages.size();
    }

// ===== Controller (<--View) 가 쓰는 메소드 (유지) =====

    public void updateActionButton(String text, boolean isEnabled) {
        lobbyActionButton.setText(text);
        lobbyActionButton.setEnabled(isEnabled);
    }

    public void setActionButtonListener(ActionListener listener) {
        lobbyActionButton.addActionListener(listener);
    }

    public void setOptionEditable(boolean isEditable) {
        optionPanel.setEditable(isEditable);
    }

    //
    public void setThemeSelectListener(Consumer<Theme> listener) {
        optionPanel.setThemeSelectListener(listener);
    }

    public void setChatEnterListener(ActionListener listener) {
        chatPanel.setChatEnterListener(listener);
    }
}