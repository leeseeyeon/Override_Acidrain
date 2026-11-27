package client.lobby;

import java.awt.*;
import java.awt.event.*;
import java.util.List;   // java.awt.List와 이름이 같아서 직접 지정
import java.util.function.*;

import javax.swing.*;

import client.lobby.panel.ChatPanel;
import client.lobby.panel.OptionPanel;
import client.lobby.panel.PlayerCardPanel;
import common.message.*;
import common.type.Theme;

public class LobbyView extends JPanel implements LobbyModelListener {
	private static final Color ACTION_BUTTON_ENABLED_COLOR = new Color(70, 99, 196);       // 버튼 활성화
    private static final Color ACTION_BUTTON_DISABLED_COLOR = new Color(212, 212, 212);  // 버튼 비활성화
    private static final Color LOBBY_BACKGROUND_COLOR = new Color(226, 232, 240);   
	
	private JLabel titleLabel;
    private PlayerCardPanel hostCardPanel;
    private OptionPanel optionPanel;
    private PlayerCardPanel guestCardPanel;
    private ChatPanel chatPanel;
    private JButton lobbyActionButton;

    private int shownChatMessageCount = 0;

    public LobbyView() {
        setBackground(LOBBY_BACKGROUND_COLOR);
        setPreferredSize(new Dimension(1000, 760));
        setLayout(null);

        titleLabel = new JLabel("대기실");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 26));
        titleLabel.setBounds(0, 15, 1000, 40);
        add(titleLabel);

        hostCardPanel = new PlayerCardPanel();
        hostCardPanel.setSlotTitle("방장");
        hostCardPanel.setBounds(40, 70, 220, 400);
        add(hostCardPanel);

        optionPanel = new OptionPanel();
        optionPanel.setBounds(280, 70, 440, 400);
        add(optionPanel);

        guestCardPanel = new PlayerCardPanel();
        guestCardPanel.setSlotTitle("게스트");
        guestCardPanel.setBounds(740, 70, 220, 400);
        add(guestCardPanel);

        chatPanel = new ChatPanel();
        chatPanel.setBounds(40, 488, 920, 170);
        add(chatPanel);

        lobbyActionButton = new JButton("준비");   
        lobbyActionButton.setForeground(Color.WHITE);
        lobbyActionButton.setBackground(ACTION_BUTTON_ENABLED_COLOR);
        lobbyActionButton.setContentAreaFilled(false);  
        lobbyActionButton.setOpaque(true);    
        lobbyActionButton.setBorderPainted(false);
        lobbyActionButton.setFocusPainted(false);
        lobbyActionButton.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        lobbyActionButton.setBounds(367, 681, 279, 58);
        add(lobbyActionButton);
    }

//Model (--> View 옵저버) 로부터 알림
    @Override
    public void onLobbyChanged(LobbyModel model) {
        showHostCard(model);
        showGuestCard(model);
        optionPanel.showSelectedTheme(model.getSelectedTheme());
        showNewChatMessages(model.getChatMessages());
    }

    private void showHostCard(LobbyModel model) {
        hostCardPanel.setNickname(model.getHostNickname());
        hostCardPanel.showStatusText("");  
    }

    private void showGuestCard(LobbyModel model) {
        guestCardPanel.setNickname(model.getGuestNickname());
        if (model.getGuestNickname() == null) {
            guestCardPanel.showStatusText("");
        } else if (!model.isLocalPlayerHost()) {
            guestCardPanel.showStatusText(""); 
        } else {
            guestCardPanel.showReadyBadge(model.isGuestReady());
        }
    }

    private void showNewChatMessages(List<ChatMessage> chatMessages) {
        if (chatMessages.size() < shownChatMessageCount) {   
            chatPanel.clearChatMessages();
            shownChatMessageCount = 0;
        }
        for (int i = shownChatMessageCount; i < chatMessages.size(); i++) {
            chatPanel.appendChatMessage(chatMessages.get(i));
        }
        shownChatMessageCount = chatMessages.size();
    }

// Controller (<--View) 사용 메소드
    public void updateActionButton(String text, boolean isEnabled) {
        lobbyActionButton.setText(text);
        lobbyActionButton.setBackground(isEnabled ? ACTION_BUTTON_ENABLED_COLOR : ACTION_BUTTON_DISABLED_COLOR); 
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